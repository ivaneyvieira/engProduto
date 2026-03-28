package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.vendaRef.ITabResumoPgto
import br.com.astrosoft.produto.viewmodel.vendaRef.TabResumoPgtoViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
import com.github.mvysny.kaributools.sortProperty
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.addons.componentfactory.monthpicker.MonthPicker
import java.time.LocalDate
import java.time.YearMonth

class TabResumoPgto(val viewModel: TabResumoPgtoViewModel) :
  TabPanelGrid<NotaResumoPgto>(NotaResumoPgto::class), ITabResumoPgto {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var chkLoja: Checkbox
  private lateinit var cmbData: Select<AgrupaData>

  private lateinit var chkParcela: Checkbox
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var cmbMesInicial: MonthPicker
  private lateinit var cmbMesFinal: MonthPicker
  private lateinit var cmbAnoInicial: IntegerField
  private lateinit var cmbAnoFinal: IntegerField

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
    verticalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = false

      horizontalLayout {
        this.isMargin = false
        this.isPadding = false
        this.isSpacing = true

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
        cmbData = select("Agrupa Data") {
          this.setItems(AgrupaData.entries)
          this.value = AgrupaData.DIA
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          addValueChangeListener {
            if (it.isFromClient) {
              gridPanel.getColumnBy(NotaResumoPgto::dataFormatada).setHeader(
                when (it.value) {
                  AgrupaData.DIA -> "Data"
                  AgrupaData.MES -> "Mês"
                  AgrupaData.ANO -> "Ano"
                }
              )

              when (it.value) {
                AgrupaData.DIA -> {
                  edtDataInicial.isVisible = true
                  edtDataFinal.isVisible = true
                  cmbMesInicial.isVisible = false
                  cmbMesFinal.isVisible = false
                  cmbAnoInicial.isVisible = false
                  cmbAnoFinal.isVisible = false
                }

                AgrupaData.MES -> {
                  edtDataInicial.isVisible = false
                  edtDataFinal.isVisible = false
                  cmbMesInicial.isVisible = true
                  cmbMesFinal.isVisible = true
                  cmbAnoInicial.isVisible = false
                  cmbAnoFinal.isVisible = false
                  val dataI = edtDataInicial.value ?: LocalDate.now()
                  val dataF = edtDataFinal.value ?: LocalDate.now()
                  cmbMesInicial.value = YearMonth.of(dataI.year, dataI.monthValue)
                  cmbMesFinal.value = YearMonth.of(dataF.year, dataF.monthValue)
                }

                AgrupaData.ANO -> {
                  edtDataInicial.isVisible = false
                  edtDataFinal.isVisible = false
                  cmbMesInicial.isVisible = false
                  cmbMesFinal.isVisible = false
                  cmbAnoInicial.isVisible = true
                  cmbAnoFinal.isVisible = true
                  val dataI = edtDataInicial.value ?: LocalDate.now()
                  val dataF = edtDataFinal.value ?: LocalDate.now()
                  cmbAnoInicial.value = dataI.year
                  cmbAnoFinal.value = dataF.year
                }
              }

              viewModel.updateView()
            }
          }
        }
      }

      horizontalLayout {
        this.isMargin = false
        this.isPadding = false
        this.isSpacing = true

        edtPesquisa = textField("Pesquisa") {
          this.width = "150px"
          valueChangeMode = ValueChangeMode.LAZY
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

        cmbMesInicial = MonthPicker().apply {
          this.isVisible = false
          this.label = "Mês Inicial"
          this.seti18n(
            MonthPicker.MonthPickerI18n()
              .setMonthNames(
                listOf(
                  "Janeiro",
                  "Fevereiro",
                  "Março",
                  "Abril",
                  "Maio",
                  "Junho",
                  "Julho",
                  "Agosto",
                  "Setembro",
                  "Outubro",
                  "Novembro",
                  "Dezembro"
                )
              )
              .setMonthLabels(
                listOf(
                  "Jan",
                  "Fev",
                  "Mar",
                  "Abr",
                  "Mai",
                  "Jun",
                  "Jul",
                  "Ago",
                  "Set",
                  "Out",
                  "Nov",
                  "Dez"
                )
              )
              .setFormat("MM/YYYY")
          )
          this.value = YearMonth.now()
          addValueChangeListener {
            if (it.isFromClient) {
              viewModel.updateView()
            }
          }
        }
        cmbMesFinal = MonthPicker().apply {
          this.isVisible = false
          this.label = "Mês Final"
          this.seti18n(
            MonthPicker.MonthPickerI18n()
              .setMonthNames(
                listOf(
                  "Janeiro",
                  "Fevereiro",
                  "Março",
                  "Abril",
                  "Maio",
                  "Junho",
                  "Julho",
                  "Agosto",
                  "Setembro",
                  "Outubro",
                  "Novembro",
                  "Dezembro"
                )
              )
              .setMonthLabels(
                listOf(
                  "Jan",
                  "Fev",
                  "Mar",
                  "Abr",
                  "Mai",
                  "Jun",
                  "Jul",
                  "Ago",
                  "Set",
                  "Out",
                  "Nov",
                  "Dez"
                )
              )
              .setFormat("MM/YYYY")
          )
          this.value = YearMonth.now()
          addValueChangeListener {
            if (it.isFromClient) {
              viewModel.updateView()
            }
          }
        }

        add(cmbMesInicial, cmbMesFinal)

        cmbAnoInicial = integerField("Ano Inicial") {
          this.value = LocalDate.now().year
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.isVisible = false
          this.width = "5rem"
          this.valueChangeMode = ValueChangeMode.LAZY
          addValueChangeListener {
            if (it.isFromClient) {
              val value = it.value
              if (value in 1900..<2100) {
                viewModel.updateView()
              }
            }
          }
        }

        cmbAnoFinal = integerField("Ano Final") {
          this.value = LocalDate.now().year
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.isVisible = false
          this.width = "5rem"
          this.valueChangeMode = ValueChangeMode.LAZY
          addValueChangeListener {
            if (it.isFromClient) {
              val value = it.value
              if (value in 1900..<2100) {
                viewModel.updateView()
              }
            }
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
    }
  }

  override fun Grid<NotaResumoPgto>.gridPanel() {
    this.addClassName("styling")
    this.setSelectionMode(Grid.SelectionMode.MULTI)

    addColumnSeq("Seq")
    columnGrid(NotaResumoPgto::loja, header = "Loja")
    columnGrid(NotaResumoPgto::dataFormatada, header = "Data", width = null) {
      this.sortProperty = NotaResumoPgto::data
    }
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
    val grupo = cmbData.value ?: AgrupaData.DIA
    val dataI = when (grupo) {
      AgrupaData.DIA -> edtDataInicial.value ?: LocalDate.now()
      AgrupaData.MES -> cmbMesInicial.value?.atDay(1) ?: LocalDate.now().withDayOfMonth(1)
      AgrupaData.ANO -> LocalDate.of(cmbAnoInicial.value ?: LocalDate.now().year, 1, 1)
    }
    val dataF = when (grupo) {
      AgrupaData.DIA -> edtDataFinal.value ?: LocalDate.now()
      AgrupaData.MES -> cmbMesFinal.value?.atEndOfMonth() ?: LocalDate.now()
        .withDayOfMonth(LocalDate.now().lengthOfMonth())

      AgrupaData.ANO -> LocalDate.of(cmbAnoFinal.value ?: LocalDate.now().year, 12, 31)
    }

    edtDataInicial.value = dataI
    edtDataFinal.value = dataF

    return FiltroNotaResumoPgto(
      loja = cmbLoja.value?.no ?: 0,
      agrupaLojas = chkLoja.value ?: false,
      agrupaParcelas = chkParcela.value ?: false,
      agrupaDatas = grupo,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = dataI,
      dataFinal = dataF,
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