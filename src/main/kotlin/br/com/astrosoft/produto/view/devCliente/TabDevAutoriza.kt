package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevAutoriza
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevAutorizaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabDevAutoriza(val viewModel: TabDevAutorizaViewModel) : TabPanelGrid<NotaVenda>(NotaVenda::class),
  ITabDevAutoriza {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private var colunaImprimir: Grid.Column<NotaVenda>? = null
  private var dlgProduto: DlgProdutosVenda? = null

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
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
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
    button("Relatorio") {
      icon = VaadinIcon.PRINT.create()
      onClick {
        viewModel.imprimeRelatorio()
      }
    }
    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "Autoriza") {
      val vendas = itensSelecionados()
      viewModel.geraPlanilha(vendas)
    }
  }

  override fun Grid<NotaVenda>.gridPanel() {
    this.addClassName("styling")
    this.setSelectionMode(Grid.SelectionMode.MULTI)

    columnGrid(NotaVenda::loja, header = "Loja")

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      if (nota.loginSolicitacao.isNullOrBlank()) {
        DialogHelper.showError("Solicitação não autorizada")
      } else {
        dlgProduto = DlgProdutosVenda(viewModel, nota)
        dlgProduto?.showDialog {
          viewModel.updateView()
        }
      }
    }

    addColumnButton(VaadinIcon.SIGN_IN, "Autoriza Solicitação", "Solicitação") { nota ->
      val form = FormSolicitacaoNotaTroca(nota)
      DialogHelper.showForm(caption = "Solicitação de Devolução", form = form) {
        val solicitacaoTroca = form.solicitacaoTroca
        viewModel.autorizaSolicitacao(nota, solicitacaoTroca)
      }
    }
    columnGrid(NotaVenda::loginSolicitacao, header = "Solicitação")

    columnGrid(NotaVenda::loginTroca, header = "Autorização")
    columnGrid(NotaVenda::dataNi, header = "Data", width = "6rem")
    columnGrid(NotaVenda::ni, header = "NI", width = "5rem")
    columnGrid(NotaVenda::valorNi, header = "Valor Dev")
    columnGrid(NotaVenda::data, header = "Data", width = "6rem")
    columnGrid(NotaVenda::nota, header = "NF", width = "6rem").right()
    columnGrid(NotaVenda::notaEntrega, header = "NF Ent", width = "6rem").right()
    columnGrid(NotaVenda::uf, header = "UF")
    columnGrid(NotaVenda::tipoNf, header = "Tipo NF") {
      this.setFooter(Html("\"<b><span style=\"font-size: medium; \">Total</span></b>\""))
    }
    val valorCol = columnGrid(NotaVenda::valor, header = "Valor NF")
    columnGrid(NotaVenda::cliente, header = "Cód Cli")
    columnGrid(NotaVenda::nomeCliente, header = "Nome Cliente").expand()
    columnGrid(NotaVenda::vendedor, header = "Vendedor").expand()
    columnGrid(NotaVenda::pdv, header = "PDV")
    columnGrid(NotaVenda::transacao, header = "Transacao")

    this.setPartNameGenerator {
      if (it.ni == null) {
        null
      } else {
        "amarelo"
      }
    }

    this.dataProvider.addDataProviderListener {
      val list = it.source.fetchAll()
      val totalValor = list.groupBy { nota ->
        "${nota.loja} ${nota.pdv} ${nota.transacao}"
      }
        .values.sumOf { t -> t.firstOrNull()?.valor ?: 0.0 }
      val totalValorTipo = list.sumOf { t -> t.valorTipo ?: 0.0 }
      valorCol.setFooter(Html("<b><font size=4>${totalValor.format()}</font></b>"))
    }
  }

  override fun filtro(): FiltroNotaVenda {
    return FiltroNotaVenda(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updateNotas(notas: List<NotaVenda>) {
    this.updateGrid(notas)
  }

  override fun itensNotasSelecionados(): List<NotaVenda> {
    return itensSelecionados()
  }

  override fun formAutoriza(nota: NotaVenda) {
    val form = FormAutorizaNota()
    DialogHelper.showForm(caption = "Autoriza Devolução", form = form) {
      viewModel.autorizaNota(nota, form.login, form.senha)
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
    return username?.devCliAutoriza == true
  }

  override val label: String
    get() = "Autoriza"

  override fun updateComponent() {
    viewModel.updateView()
  }
}