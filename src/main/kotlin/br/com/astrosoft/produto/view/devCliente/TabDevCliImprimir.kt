package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevCliImprimir
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCliImprimirViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabDevCliImprimir(val viewModel: TabDevCliImprimirViewModel) :
  TabPanelGrid<EntradaDevCli>(EntradaDevCli::class),
  ITabDevCliImprimir {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private var dlgProduto: DlgProdutosImprimir? = null

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
  }

  override fun Grid<EntradaDevCli>.gridPanel() {
    this.addClassName("styling")

    this.addItemClickListener {
        when {
          it.column.key == EntradaDevCli::liberaStr .name                              -> {
            val liberaImpressao = it.item.liberaImpressao ?: ""
            if(liberaImpressao == "S"){
              it.item.liberaImpressao = "N"
            }else{
              it.item.liberaImpressao = "S"
            }
            viewModel.salvaLiberaPedido(it.item)
            this.dataProvider.refreshAll()
          }
        }
      }

    columnGrid(EntradaDevCli::loja, header = "Loja")
    addColumnButton(VaadinIcon.PRINT, "Imprimir vale troca", "Imprimir") { nota ->
      viewModel.imprimeValeTroca(nota)
    }
    val user = AppConfig.userLogin() as? UserSaci
    if (user?.liberaImpressao == true) {
      columnGrid(EntradaDevCli::liberaStr, header = "Libera")
    }
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      val notasAutoriza = nota.notaAtuoriza()
      if (notasAutoriza.isEmpty()) {
        DialogHelper.showError("Nota de autorização não localizada ")
      } else {
        val notaLocalizada = notasAutoriza.firstOrNull() ?: return@addColumnButton

        if (notaLocalizada.loginSolicitacao.isNullOrBlank()) {
          DialogHelper.showError("Solicitação não autorizada")
        } else {
          dlgProduto = DlgProdutosImprimir(viewModel, notaLocalizada)
          dlgProduto?.showDialog {
            viewModel.updateView()
          }
        }
      }
    }
    columnGrid(EntradaDevCli::loginAutorizacao, header = "Autorização")
    columnGrid(EntradaDevCli::loginSolicitacao, header = "Assina Troca")
    columnGrid(EntradaDevCli::invno, header = "NI")
    columnGrid(EntradaDevCli::notaFiscal, header = "NF Dev")
    columnGrid(EntradaDevCli::data, header = "Data")
    columnGrid(EntradaDevCli::vendno, header = "Cód For")
    columnGrid(EntradaDevCli::fornecedor, header = "Fornecedor")
    columnGrid(EntradaDevCli::valor, header = "Valor Devolução")
    columnGrid(EntradaDevCli::nfVendaVenda, header = "NF Fatura")
    columnGrid(EntradaDevCli::nfVenda, header = "Nota Venda")
    columnGrid(EntradaDevCli::nfData, header = "Data")
    columnGrid(EntradaDevCli::custno, header = "Cód Cliente")
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
    val form = FormAutorizaNota()
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

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devCliImprimir == true
  }

  override val label: String
    get() = "VC Imprimir"

  override fun updateComponent() {
    viewModel.updateView()
  }
}