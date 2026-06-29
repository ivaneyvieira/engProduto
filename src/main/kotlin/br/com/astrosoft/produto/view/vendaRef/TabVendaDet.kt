package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.FiltroNotaVendaDet
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaVendaDet
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.vendaRef.ITabVendaDet
import br.com.astrosoft.produto.viewmodel.vendaRef.TabVendaDetViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabVendaDet(val viewModel: TabVendaDetViewModel) :
  TabPanelGrid<NotaVendaDet>(NotaVendaDet::class), ITabVendaDet {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private var dlgProduto: DlgProdutosVendaDet? = null
  private var dlgProdutoPgto: DlgProdutosVendaDetPgto? = null

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
      this.width = "6rem"
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
    button("Relatorio") {
      icon = VaadinIcon.PRINT.create()
      onClick {
        viewModel.imprimeRelatorio()
      }
    }
    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "vendas") {
      val vendas = itensSelecionados()
      viewModel.geraPlanilha(vendas)
    }
  }

  override fun Grid<NotaVendaDet>.gridPanel() {
    this.addClassName("styling")
    this.setSelectionMode(Grid.SelectionMode.MULTI)

    addColumnSeq("Seq")
    columnGrid(NotaVendaDet::loja, header = "Loja")

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosVendaDet(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }

    addColumnButton(VaadinIcon.FILE_TABLE, "Pagamento Detalhado", "Pgto Det") { nota ->
      dlgProdutoPgto = DlgProdutosVendaDetPgto(viewModel, nota)
      dlgProdutoPgto?.showDialog {
        viewModel.updateView()
      }
    }

    columnGrid(NotaVendaDet::pedido, header = "Pedido")
    columnGrid(NotaVendaDet::pdv, header = "PDV")
    columnGrid(NotaVendaDet::data, header = "Data")
    columnGrid(NotaVendaDet::transacao, header = "Transação")
    columnGrid(NotaVendaDet::nota, header = "NF")
    columnGrid(NotaVendaDet::uf, header = "UF")
    columnGrid(NotaVendaDet::tipoNf, header = "Tipo NF")
    columnGrid(NotaVendaDet::hora, header = "Hora")
    columnGrid(NotaVendaDet::numeroInterno, header = "NI", width = "100px")
    columnGrid(NotaVendaDet::numMetodo, header = "Met")
    columnGrid(NotaVendaDet::nomeMetodo, header = "Nome Met")
    columnGrid(NotaVendaDet::mult, pattern = "#,##0.0000", header = "Mlt")

    val valorCol = columnGrid(NotaVendaDet::valor, header = "Valor NF")
    columnGrid(NotaVendaDet::cliente, header = "Cód Cli")
    columnGrid(NotaVendaDet::nomeCliente, header = "Nome Cliente").expand()
    columnGrid(NotaVendaDet::vendedor, header = "Vendedor").expand()

    this.dataProvider.addDataProviderListener {
      val list = it.source.fetchAll()
      val totalValor = list.groupBy { nota ->
        "${nota.loja} ${nota.pdv} ${nota.transacao}"
      }
        .values.sumOf { t -> t.firstOrNull()?.valor ?: 0.0 }
      valorCol.setFooter(Html("<b><font size=4>${totalValor.format()}</font></b>"))
    }
  }

  override fun filtro(): FiltroNotaVendaDet {
    return FiltroNotaVendaDet(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updateNotas(notas: List<NotaVendaDet>) {
    this.updateGrid(notas)
  }

  override fun itensNotasSelecionados(): List<NotaVendaDet> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.tabVendaDet == true
  }

  override val label: String
    get() = "Pgto Det"

  override fun updateComponent() {
    viewModel.updateView()
  }
}