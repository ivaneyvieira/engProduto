package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.FiltroNotaResumo
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaResumo
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.vendaRef.ITabResumo
import br.com.astrosoft.produto.viewmodel.vendaRef.TabResumoViewModel
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

class TabResumo(val viewModel: TabResumoViewModel) :
  TabPanelGrid<NotaResumo>(NotaResumo::class), ITabResumo {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private var dlgProduto: DlgProdutosResumo? = null

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

  override fun Grid<NotaResumo>.gridPanel() {
    this.addClassName("styling")
    this.setSelectionMode(Grid.SelectionMode.MULTI)

    addColumnSeq("Seq")
    columnGrid(NotaResumo::loja, header = "Loja")
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosResumo(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(NotaResumo::pedido, header = "Pedido")
    columnGrid(NotaResumo::pdv, header = "PDV")
    columnGrid(NotaResumo::data, header = "Data")
    columnGrid(NotaResumo::transacao, header = "Transação")
    columnGrid(NotaResumo::nota, header = "NF")
    columnGrid(NotaResumo::uf, header = "UF")
    columnGrid(NotaResumo::tipoNf, header = "Tipo NF")
    columnGrid(NotaResumo::hora, header = "Hora")
    columnGrid(NotaResumo::numeroInterno, header = "NI", width = "100px")
    columnGrid(NotaResumo::numMetodo, header = "Met")
    columnGrid(NotaResumo::nomeMetodo, header = "Nome Met")
    columnGrid(NotaResumo::mult, pattern = "#,##0.0000", header = "Mlt")
    columnGrid(NotaResumo::documento, header = "Documento")
    columnGrid(NotaResumo::quantParcelas, header = "Parc")
    columnGrid(NotaResumo::mediaPrazo, header = "Pz M")
    columnGrid(NotaResumo::tipoPgto, header = "Tipo Pgto") {
      this.setFooter(Html("<b><font size=4>Total</font></b>"))
    }
    val valorCol = columnGrid(NotaResumo::valor, header = "Valor NF")
    val valorTipoCol = columnGrid(NotaResumo::valorTipo, header = "Valor TP")
    columnGrid(NotaResumo::cliente, header = "Cód Cli")
    columnGrid(NotaResumo::nomeCliente, header = "Nome Cliente").expand()
    columnGrid(NotaResumo::vendedor, header = "Vendedor").expand()

    this.dataProvider.addDataProviderListener {
      val list = it.source.fetchAll()
      val totalValor = list.groupBy { nota ->
        "${nota.loja} ${nota.pdv} ${nota.transacao}"
      }
        .values.sumOf { t -> t.firstOrNull()?.valor ?: 0.0 }
      val totalValorTipo = list.sumOf { t -> t.valorTipo ?: 0.0 }
      valorCol.setFooter(Html("<b><font size=4>${totalValor.format()}</font></b>"))
      valorTipoCol.setFooter(Html("<b><font size=4>${totalValorTipo.format()}</font></b>"))
    }
  }

  override fun filtro(): FiltroNotaResumo {
    return FiltroNotaResumo(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updateNotas(notas: List<NotaResumo>) {
    this.updateGrid(notas)
  }

  override fun itensNotasSelecionados(): List<NotaResumo> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.tabResumo == true
  }

  override val label: String
    get() = "Resumo"

  override fun updateComponent() {
    viewModel.updateView()
  }
}