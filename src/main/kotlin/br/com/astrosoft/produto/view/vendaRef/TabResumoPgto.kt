package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.FiltroNotaResumoPgto
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaResumoPgto
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.vendaRef.ITabResumoPgto
import br.com.astrosoft.produto.viewmodel.vendaRef.TabResumoPgtoViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
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

class TabResumoPgto(val viewModel: TabResumoPgtoViewModel) :
  TabPanelGrid<NotaResumoPgto>(NotaResumoPgto::class), ITabResumoPgto {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var chkLoja: Checkbox
  private lateinit var chkParcela: Checkbox
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

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
        if (it.isFromClient) {
          viewModel.updateView()
        }
      }
    }
    init()
    chkLoja = checkBox("Agrupa Lojas") {
      addValueChangeListener {
        if (it.isFromClient) {
          viewModel.updateView()
          gridPanel.getColumnBy(NotaResumoPgto::loja).isVisible = !(it.value ?: false)
        }
      }
    }
    chkParcela = checkBox("Agrupa Pz M") {
      addValueChangeListener {
        if (it.isFromClient) {
          viewModel.updateView()
          val visivel = !(it.value ?: false)
          gridPanel.getColumnBy(NotaResumoPgto::numMetodo).isVisible = visivel
          gridPanel.getColumnBy(NotaResumoPgto::nomeMetodo).isVisible = visivel
          gridPanel.getColumnBy(NotaResumoPgto::mult).isVisible = visivel
          gridPanel.getColumnBy(NotaResumoPgto::quantParcelas).isVisible = visivel
          gridPanel.getColumnBy(NotaResumoPgto::tipoPgto).isVisible = visivel
        }
      }
    }
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

  override fun Grid<NotaResumoPgto>.gridPanel() {
    this.addClassName("styling")
    this.setSelectionMode(Grid.SelectionMode.MULTI)

    addColumnSeq("Seq")
    columnGrid(NotaResumoPgto::loja, header = "Loja")
    columnGrid(NotaResumoPgto::data, header = "Data", width = null)
    columnGrid(NotaResumoPgto::numMetodo, header = "Met")
    columnGrid(NotaResumoPgto::nomeMetodo, header = "Nome Met")
    columnGrid(NotaResumoPgto::mult, pattern = "#,##0.0000", header = "Mlt")
    //columnGrid(NotaResumoPgto::documento, header = "Documento")
    columnGrid(NotaResumoPgto::quantParcelas, header = "Parc")
    columnGrid(NotaResumoPgto::mediaPrazo, header = "Pz M")
    columnGrid(NotaResumoPgto::tipoPgto, header = "Tipo Pgto")
    // val valorCol = columnGrid(NotaResumoPgto::valor, header = "Valor NF")

    columnGrid(NotaResumoPgto::valorFin, header = "Fin")
    columnGrid(NotaResumoPgto::valorTipo, header = "Valor Total")
    columnGrid(NotaResumoPgto::perVenda, header = "% Venda")

    this.dataProvider.addDataProviderListener {
      val list = it.source.fetchAll()
      val totalValorTipo = list.sumOf { t -> (t.valorTipo ?: 0.0) }
      val totalParcelas = list.sumOf { t -> (t.mediaPrazo ?: 0) * (t.valorTipo ?: 0.00) } /
                          list.sumOf { t -> t.valorTipo ?: 0.00 }
      val totalFin = list.sumOf { t -> (t.valorFin ?: 0.0) }
      val totalPerVenda = list.sumOf { t -> (t.perVenda ?: 0.0) }
      getColumnBy(NotaResumoPgto::mediaPrazo).setFooter(Html("<b><font size=4>${totalParcelas.format()}</font></b>"))
      getColumnBy(NotaResumoPgto::valorTipo).setFooter(Html("<b><font size=4>${totalValorTipo.format()}</font></b>"))
      getColumnBy(NotaResumoPgto::valorFin).setFooter(Html("<b><font size=4>${totalFin.format()}</font></b>"))
      getColumnBy(NotaResumoPgto::perVenda).setFooter(Html("<b><font size=4>${totalPerVenda.format()}</font></b>"))
    }
    this.dataProvider.addDataProviderListener {
      this.recalculateColumnWidths()
    }
  }

  override fun filtro(): FiltroNotaResumoPgto {
    return FiltroNotaResumoPgto(
      loja = cmbLoja.value?.no ?: 0,
      agrupaLojas = chkLoja.value ?: false,
      agrupaParcelas = chkParcela.value ?: false,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updateNotas(notas: List<NotaResumoPgto>) {
    this.updateGrid(notas)
  }

  override fun itensNotasSelecionados(): List<NotaResumoPgto> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.tabResumoPgto == true
  }

  override val label: String
    get() = "Resumo Pgto"

  override fun updateComponent() {
    viewModel.updateView()
  }
}