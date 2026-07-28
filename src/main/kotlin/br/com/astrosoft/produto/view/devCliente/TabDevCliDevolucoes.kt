package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevCliDevolucoes
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCliDevolucoesViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabDevCliDevolucoes(val viewModel: TabDevCliDevolucoesViewModel) :
  TabPanelGrid<EntradaDevCli>(EntradaDevCli::class), ITabDevCliDevolucoes {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private var dlgProduto: DlgProdutosVendaDevoluccao? = null

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaVale != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaVale ?: 0) ?: Loja.lojaZero
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.impressoraDev.orEmpty().toList()
  }

  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
      }
    }
    init()
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data inicial") {
      this.localePtBr()
      this.value = LocalDate.now()

      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.value = LocalDate.now()

      addValueChangeListener {
        viewModel.updateView()
      }
    }

    button("Atualiza Crédito Dev") {
      this.onClick {
        viewModel.atualizaCredito()
      }
    }
  }

  override fun Grid<EntradaDevCli>.gridPanel() {
    this.addClassName("styling")

    this.addItemClickListener {
      when {
        it.column.key == EntradaDevCli::liberaStr.name -> {
          val liberaImpressao = it.item.liberaImpressao ?: ""
          if (liberaImpressao == "S") {
            it.item.liberaImpressao = "N"
          } else {
            it.item.liberaImpressao = "S"
          }
          viewModel.salvaLiberaPedido(it.item)
          this.dataProvider.refreshAll()
        }
      }
    }

    columnGrid(EntradaDevCli::loja, header = "Loja")
    addColumnButton(iconButton = VaadinIcon.PRINT, tooltip = "Imprimir vale troca", header = "Imprimir") { nota ->
      viewModel.imprimeValeTroca(nota)
    }

    val user = AppConfig.userLogin() as? UserSaci

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      if (nota.loginSolicitacao.isNullOrBlank()) {
        DialogHelper.showWarning("Devolução sem autorização")
      } else {
        val notasAutoriza = nota.notaAutoriza().filter {venda ->
          venda.ni == nota.invno
        }
        if (notasAutoriza.isEmpty()) {
          DialogHelper.showWarning("Nota de autorização não localizada")
        } else {
          val notaLocalizada = notasAutoriza.firstOrNull() ?: return@addColumnButton

          if (notaLocalizada.loginSolicitacao.isNullOrBlank()) {
            DialogHelper.showWarning("Solicitação não autorizada")
          } else {
            dlgProduto = DlgProdutosVendaDevoluccao(viewModel, notaLocalizada)
            dlgProduto?.showDialog {
              viewModel.updateView()
            }
          }
        }
      }
    }

    addColumnButton(
      iconButton = VaadinIcon.SIGN_IN,
      tooltip = "Autoriza Solicitação",
      header = "Solicitação"
    ) { nota: EntradaDevCli ->
      val form = FormSolicitacaoDevolucaoTroca(nota)

      DialogHelper.showForm(caption = "Autoriza Devolução", form = form) {
        val result = form.validaFiltro()
        result.onFailure {
          DialogHelper.showWarning(it.message ?: "Erro no filtro")
        }
        result.onSuccess { solicitacaoTroca ->
          val solicitacaoTroca: SolicitacaoTroca = solicitacaoTroca
          viewModel.autorizaSolicitacao(nota, solicitacaoTroca)
        }
      }
    }

    if (user?.defazSolicitacao == true) {
      addColumnButton(VaadinIcon.TRASH, "Desfazer Solicitação", "Desfaz") { nota: EntradaDevCli ->
        if (nota.loginSolicitacao.isNullOrBlank()) {
          DialogHelper.showError("Não existe solicitação para desfazer")
        } else {
          DialogHelper.showQuestion("Desfaz a solicitação?") {
            viewModel.desfazSolicitacao(nota)
          }
        }
      }
    }

    columnGrid(EntradaDevCli::loginSolicitacao, header = "Autorização")
    columnGrid(EntradaDevCli::loginAutorizacao, header = "Assina Troca")
    columnGrid(EntradaDevCli::tipoObs, header = "Observação") {
      this.setPartNameGenerator() { nota ->
        if (nota.nameCli.isNullOrBlank()) {
          if (nota.tipoObs.contains("MUDA")) {
            "vermelho"
          } else {
            null
          }
        } else {
          null
        }
      }
    }
    if (user?.admin == true) {
      columnGrid(EntradaDevCli::cliMuda, header = "Muda Cli")
    }
    columnGrid(EntradaDevCli::invno, header = "NI")
    columnGrid(EntradaDevCli::notaFiscal, header = "NF Dev")
    columnGrid(EntradaDevCli::data, header = "Data")
    columnGrid(EntradaDevCli::vendno, header = "Cód For")
    columnGrid(EntradaDevCli::fornecedor, header = "Fornecedor")
    columnGrid(EntradaDevCli::valor, header = "Valor Devolução")
    columnGrid(EntradaDevCli::nfVendaVenda, header = "NF Fatura")
    columnGrid(EntradaDevCli::nfVenda, header = "Nota Venda")
    columnGrid(EntradaDevCli::nfData, header = "Data")
    columnGrid(EntradaDevCli::custnoVend, header = "Cód Cliente")
    columnGrid(EntradaDevCli::cliente, header = "Nome do Cliente")
    columnGrid(EntradaDevCli::nfValor, header = "Valor Venda")
  }

  override fun filtro(): FiltroEntradaDevCli {
    val user = AppConfig.userLogin() as? UserSaci
    return FiltroEntradaDevCli(
      loja = cmbLoja.value?.no ?: 0,
      query = edtPesquisa.value ?: "",
      dataI = edtDataInicial.value,
      dataF = edtDataFinal.value,
      impresso = false,
      dataLimiteInicial = LocalDate.of(2023, 12, 1),
      tipo = ETipoDevCli.COM,
      dataCorte = user?.dataVendaDevolucao
    )
  }

  override fun updateNotas(notas: List<EntradaDevCli>) {
    updateGrid(notas)
  }

  override fun formAutoriza(nota: EntradaDevCli) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Autoriza pedido", form = form) {
      viewModel.autorizaNota(nota, form.login, form.senha)
    }
  }

  override fun ajustaProduto(nota: EntradaDevCli) {
    val form = FormAjustaProduto(nota)
    DialogHelper.showForm(caption = "Ajusta Produto", form = form) {
      form.listAjustes().forEach { ajuste ->
        viewModel.ajusteProduto(ajuste)
      }
    }
  }

  override fun fechaFormProduto() {
    dlgProduto?.fecha()
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtos(): List<ProdutoNFS> {
    return dlgProduto?.produtos().orEmpty()
  }

  override fun notasSelecionada(): List<EntradaDevCli> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devCliDevolucoes == true
  }

  override val label: String
    get() = "Dev Cli"

  override fun updateComponent() {
    viewModel.updateView()
  }
}