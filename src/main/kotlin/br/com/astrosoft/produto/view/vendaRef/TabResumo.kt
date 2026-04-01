package br.com.astrosoft.produto.view.vendaRef

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.helper.monthPicker
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.vendaRef.ITabResumo
import br.com.astrosoft.produto.viewmodel.vendaRef.TabResumoViewModel
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
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.addons.componentfactory.monthpicker.MonthPicker
import java.time.LocalDate
import java.time.YearMonth

class TabResumo(val viewModel: TabResumoViewModel) :
  TabPanelGrid<NotaResumo>(NotaResumo::class), ITabResumo {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var chkLoja: Checkbox
  private lateinit var edtPesquisa: TextField
  private lateinit var cmbData: Select<AgrupaData>
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
            if (it.isFromClient)
              viewModel.updateView()
          }
        }
        init()
        chkLoja = checkBox("Agrupa Lojas") {
          addValueChangeListener {
            if (it.isFromClient) {
              viewModel.updateView()
              gridPanel.getColumnBy(NotaResumo::loja).isVisible = !(it.value ?: false)
            }
          }
        }

        cmbData = select("Agrupa Data") {
          this.width = "6rem"
          this.setItems(AgrupaData.entries)
          this.value = AgrupaData.DIA
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          addValueChangeListener {
            if (it.isFromClient) {
              gridPanel.getColumnBy(NotaResumo::dataFormatada).setHeader(
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
          this.width = "10rem"
          valueChangeMode = ValueChangeMode.LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtDataInicial = datePicker("Data inicial") {
          this.localePtBr()
          this.width = "8rem"
          this.value = LocalDate.now()
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        edtDataFinal = datePicker("Data Final") {
          this.localePtBr()
          this.width = "8rem"
          this.value = LocalDate.now()
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        cmbMesInicial = monthPicker("Mês Inicial") {
          this.isVisible = false
          this.value = YearMonth.now()
          addValueChangeListener {
            if (it.isFromClient) {
              viewModel.updateView()
            }
          }
        }
        cmbMesFinal = monthPicker("Mês Final") {
          this.isVisible = false
          this.value = YearMonth.now()
          addValueChangeListener {
            if (it.isFromClient) {
              viewModel.updateView()
            }
          }
        }

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

  override fun Grid<NotaResumo>.gridPanel() {
    this.addClassName("styling")
    this.setSelectionMode(Grid.SelectionMode.MULTI)

    addColumnSeq("Seq")
    columnGrid(NotaResumo::loja, header = "Loja")
    columnGrid(NotaResumo::dataFormatada, header = "Data")
    columnGrid(NotaResumo::numMetodo, header = "Met")
    columnGrid(NotaResumo::nomeMetodo, header = "Nome Met")
    columnGrid(NotaResumo::mult, pattern = "#,##0.0000", header = "Mlt")
    columnGrid(NotaResumo::documento, header = "Documento")
    columnGrid(NotaResumo::quantParcelas, header = "Parc")
    columnGrid(NotaResumo::mediaPrazo, header = "Pz M")
    columnGrid(NotaResumo::tipoPgto, header = "Tipo Pgto")
    columnGrid(NotaResumo::valor, header = "Valor NF")
    columnGrid(NotaResumo::valorTipo, header = "Valor TP")

    this.dataProvider.addDataProviderListener {
      val list = it.source.fetchAll()
      val totalValorTipo = list.sumOf { t -> (t.valorTipo ?: 0.0) }
      val totalParcelas = list.sumOf { t -> (t.mediaPrazo ?: 0.00) * (t.valorTipo ?: 0.00) } /
                          list.sumOf { t -> t.valorTipo ?: 0.00 }
      val totalFin = list.sumOf { t -> (t.valorFin ?: 0.0) }
      val mediaMult = totalValorTipo / (totalValorTipo - totalFin)
      getColumnBy(NotaResumo::mediaPrazo).setFooter(Html("<b><font size=4>${totalParcelas.format()}</font></b>"))
      getColumnBy(NotaResumo::valorTipo).setFooter(Html("<b><font size=4>${totalValorTipo.format()}</font></b>"))
      //getColumnBy(NotaResumo::valorFin).setFooter(Html("<b><font size=4>${totalFin.format()}</font></b>"))
      getColumnBy(NotaResumo::mult).setFooter(Html("<b><font size=4>${mediaMult.format("#,##0.0000")}</font></b>"))
    }
    this.dataProvider.addDataProviderListener {
      this.recalculateColumnWidths()
    }
  }

  override fun filtro(): FiltroNotaResumo {
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

    return FiltroNotaResumo(
      loja = cmbLoja.value?.no ?: 0,
      agrupaLojas = chkLoja.value ?: false,
      agrupaData = cmbData.value ?: AgrupaData.DIA,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = dataI,
      dataFinal = dataF,
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