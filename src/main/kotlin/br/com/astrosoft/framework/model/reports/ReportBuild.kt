package br.com.astrosoft.framework.model.reports

import br.com.astrosoft.framework.util.toDate
import br.com.astrosoft.framework.util.toLocalDate
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.base.datatype.AbstractDataType
import net.sf.dynamicreports.report.builder.DynamicReports.*
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder
import net.sf.dynamicreports.report.builder.component.ComponentBuilder
import net.sf.dynamicreports.report.builder.subtotal.SubtotalBuilder
import net.sf.dynamicreports.report.constant.*
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.LEFT
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT
import net.sf.dynamicreports.report.constant.PageOrientation.PORTRAIT
import net.sf.dynamicreports.report.constant.PageType.A4
import net.sf.dynamicreports.report.defaults.Defaults
import net.sf.dynamicreports.report.definition.datatype.DRIDataType
import net.sf.dynamicreports.report.exception.DRException
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import org.apache.poi.ss.formula.functions.T
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.reflect.KProperty1

abstract class ReportBuild<T> {
  private val localDateType = LocalDateType()
  private val columnsMap = mutableMapOf<KProperty1<T, *>, TextColumnBuilder<*>>()
  private val columnsList = mutableListOf<TextColumnBuilder<*>>()

  abstract val propriedades: PropriedadeRelatorio

  protected open fun labelTitleCol(): TextColumnBuilder<String>? = null

  protected fun <V> column(
    dataType: DRIDataType<in V, V>,
    prop: KProperty1<T, V>,
    title: String,
    aligment: HorizontalTextAlignment,
    width: Int,
    pattern: String,
    oculto: Boolean,
    block: TextColumnBuilder<V>.() -> Unit = {}
  ): TextColumnBuilder<V> =
    col.column(if (oculto) "" else if (title == "") prop.name else title, prop.name, dataType).apply {
      this.setHorizontalTextAlignment(aligment)
      if (width > 0) this.setFixedWidth(width) else this.setMinHeight(0)
      if (pattern != "") this.setPattern(pattern)
      block()

      columnsMap[prop] = this
      columnsList.add(this)
      if (oculto) {
        this.setFixedWidth(0)
        this.setStyle(stl.style().setBackgroundColor(Color(0, 0, 0, 0)))
      }
    }

  protected fun columnInt(
    prop: KProperty1<T, Int>,
    title: String = "",
    aligment: HorizontalTextAlignment = RIGHT,
    width: Int = -1,
    pattern: String = "0",
    oculto: Boolean = false,
    block: TextColumnBuilder<Int>.() -> Unit = {}
  ): TextColumnBuilder<Int> =
    column(type.integerType(), prop, title, aligment, width, pattern, oculto, block)

  protected fun columnDouble(
    prop: KProperty1<T, Double>,
    title: String = "",
    aligment: HorizontalTextAlignment = RIGHT,
    width: Int = -1,
    pattern: String = "#,##0.00",
    oculto: Boolean = false,
    block: TextColumnBuilder<Double>.() -> Unit = {}
  ): TextColumnBuilder<Double> =
    column(type.doubleType(), prop, title, aligment, width, pattern, oculto, block)

  protected fun columnString(
    prop: KProperty1<T, String>,
    title: String = "",
    aligment: HorizontalTextAlignment = LEFT,
    width: Int = -1,
    oculto: Boolean = false,
    block: TextColumnBuilder<String>.() -> Unit = {}
  ): TextColumnBuilder<String> =
    column(type.stringType(), prop, title, aligment, width, "", oculto, block)

  protected fun columnLocalDate(
    prop: KProperty1<T, LocalDate>,
    title: String = "",
    aligment: HorizontalTextAlignment = RIGHT,
    width: Int = -1,
    pattern: String = "dd/MM/yyyy",
    oculto: Boolean = false,
    block: TextColumnBuilder<LocalDate>.() -> Unit = {}
  ): TextColumnBuilder<LocalDate> =
    column(localDateType, prop, title, aligment, width, pattern, oculto, block)

  protected fun columnDate(
    prop: KProperty1<T, Date>,
    title: String = "",
    aligment: HorizontalTextAlignment = RIGHT,
    width: Int = -1,
    pattern: String = "dd/MM/yyyy",
    oculto: Boolean = false,
    block: TextColumnBuilder<Date>.() -> Unit = {}
  ): TextColumnBuilder<Date> =
    column(type.dateDayType(), prop, title, aligment, width, pattern, oculto, block)

  protected fun columnBuilder(): List<TextColumnBuilder<out Any>> {
    return columnsList
  }

  protected open fun titleBuider(): ComponentBuilder<*, *> {
    val largura = -1
    return verticalBlock {
      horizontalList {
        text(propriedades.titulo, HorizontalTextAlignment.CENTER, largura).apply {
          this.setStyle(stl.style(Templates.fieldFontGrande).setForegroundColor(propriedades.color))
        }
      }
      if (propriedades.subTitulo != "") {
        horizontalList {
          text(propriedades.subTitulo, LEFT, largura)
        }
      }
    }
  }

  protected fun pageFooterBuilder(): ComponentBuilder<*, *>? {
    return cmp.verticalList()
  }

  protected fun subtotalBuilder(): List<SubtotalBuilder<*, *>> {
    return emptyList()
  }

  abstract fun listDataSource(): List<T>

  open fun makeReport(): JasperReportBuilder {
    val labelTitleCol = labelTitleCol()
    val itemGroup = if (labelTitleCol == null) null
    else grp.group(labelTitleCol).setTitleWidth(0).setHeaderLayout(GroupHeaderLayout.VALUE).showColumnHeaderAndFooter()
    val colunms = columnBuilder().toTypedArray()

    return report().title(titleBuider())
      .setTemplate(Templates.reportTemplate)
      .columns(* colunms)
      .columnGrid(* colunms)
      .setDataSource(listDataSource())
      .setPageFormat(propriedades.pageType, propriedades.pageOrientation)
      .setPageMargin(margin(28))
      .summary(pageFooterBuilder())
      .subtotalsAtSummary(* subtotalBuilder().toTypedArray())
      .setSubtotalStyle(stl.style().setPadding(2).setTopBorder(stl.pen1Point()))
      .pageFooter(cmp.pageNumber().setHorizontalTextAlignment(RIGHT).setStyle(stl.style().setFontSize(8)))
      .setColumnStyle(
        stl.style()
          .setFontSize(propriedades.detailFonteSize)
      ) //.setColumnTitleStyle(stl.style().setFontSize(propriedades.detailFonteSize))
      .setDetailStyle(stl.style().setFontSize(propriedades.detailFonteSize))
      .apply {
        if (itemGroup != null) this.groupBy(itemGroup).addGroupFooter(itemGroup, cmp.text("")).setShowColumnTitle(false)
      }
  }

  companion object {
    fun renderReport(printList: List<JasperPrint>): ByteArray {
      val exporter = JRPdfExporter()
      val out = ByteArrayOutputStream()
      exporter.setExporterInput(SimpleExporterInput.getInstance(printList))

      exporter.exporterOutput = SimpleOutputStreamExporterOutput(out)

      exporter.exportReport()
      return out.toByteArray()
    }
  }
}

open class LocalDateType : AbstractDataType<LocalDate, LocalDate>() {
  override fun getPattern(): String {
    return Defaults.getDefaults().dateType.pattern
  }

  override fun getHorizontalTextAlignment(): HorizontalTextAlignment {
    return Defaults.getDefaults().dateType.horizontalTextAlignment
  }

  override fun valueToString(value: LocalDate?, locale: Locale): String? {
    return if (value != null) {
      SimpleDateFormat(pattern, locale).format(value.toDate())
    } else null
  }

  @Throws(DRException::class)
  override fun stringToValue(value: String?, locale: Locale): LocalDate? {
    return if (value != null) {
      try {
        SimpleDateFormat(pattern, locale).parse(value).toLocalDate()
      } catch (e: ParseException) {
        throw DRException("Unable to convert string value to date", e)
      }
    } else null
  }

  companion object {
    private const val serialVersionUID = Constants.SERIAL_VERSION_UID
  }
}

data class PropriedadeRelatorio(
  val titulo: String,
  val subTitulo: String,
  val detailFonteSize: Int = 10,
  val color: Color = Color.BLACK,
  val pageOrientation: PageOrientation = PORTRAIT,
  val pageType: PageType = A4
)