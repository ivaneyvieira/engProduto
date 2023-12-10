package br.com.astrosoft.framework.model.reports

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.toLocalDate
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.base.datatype.AbstractDataType
import net.sf.dynamicreports.report.base.expression.AbstractValueFormatter
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
import net.sf.dynamicreports.report.definition.ReportParameters
import net.sf.dynamicreports.report.definition.datatype.DRIDataType
import net.sf.dynamicreports.report.exception.DRException
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KProperty1

abstract class ReportBuild<T> {
  private val localDateType = LocalDateType()
  private val columnsMap = mutableMapOf<KProperty1<T, *>, TextColumnBuilder<*>>()
  private val columnsList = mutableListOf<TextColumnBuilder<*>>()

  abstract fun config(itens: List<T>): PropriedadeRelatorio

  protected open fun labelTitleCol(): TextColumnBuilder<String>? = null

  @JvmName("column")
  protected fun <V : Any> columnReport(
    dataType: DRIDataType<in V, V>,
    property: KProperty1<T, V?>,
    header: String,
    aligment: HorizontalTextAlignment,
    width: Int,
    pattern: String,
    oculto: Boolean,
    block: TextColumnBuilder<V?>.() -> Unit = {}
  ): TextColumnBuilder<V> {
    return col.column(if (oculto) "" else if (header == "") property.name else header, property.name, dataType).apply {
      this.setHorizontalTextAlignment(aligment)
      if (width > 0) this.setFixedWidth(width) else this.setMinHeight(0)
      if (pattern != "") this.setPattern(pattern)
      block()

      columnsMap[property] = this
      columnsList.add(this)
      if (oculto) {
        this.setFixedWidth(0)
        this.setStyle(stl.style().setBackgroundColor(Color(0, 0, 0, 0)))
      }
    }
  }

  @JvmName("columnInt")
  protected fun columnReport(
    property: KProperty1<T, Int?>,
    header: String = "",
    aligment: HorizontalTextAlignment = RIGHT,
    width: Int = -1,
    pattern: String = "0",
    oculto: Boolean = false,
    block: TextColumnBuilder<Int?>.() -> Unit = {}
  ): TextColumnBuilder<Int> {
    return columnReport(
      dataType = type.integerType(),
      property = property,
      header = header,
      aligment = aligment,
      width = width,
      pattern = pattern,
      oculto = oculto,
      block = block
    )
  }

  @JvmName("columnDouble")
  protected fun columnReport(
    property: KProperty1<T, Double?>,
    header: String = "",
    aligment: HorizontalTextAlignment = RIGHT,
    width: Int = -1,
    pattern: String = "#,##0.00",
    oculto: Boolean = false,
    block: TextColumnBuilder<Double?>.() -> Unit = {}
  ): TextColumnBuilder<Double> {
    return columnReport(
      dataType = type.doubleType(),
      property = property,
      header = header,
      aligment = aligment,
      width = width,
      pattern = pattern,
      oculto = oculto,
      block = block
    )
  }

  @JvmName("columnString")
  protected fun columnReport(
    property: KProperty1<T, String?>,
    header: String = "",
    aligment: HorizontalTextAlignment = LEFT,
    width: Int = -1,
    oculto: Boolean = false,
    block: TextColumnBuilder<String?>.() -> Unit = {}
  ): TextColumnBuilder<String> {
    return columnReport(
      dataType = type.stringType(),
      property = property,
      header = header,
      aligment = aligment,
      width = width,
      pattern = "",
      oculto = oculto,
      block = block
    )
  }

  @JvmName("columnLocalDate")
  protected fun columnReport(
    property: KProperty1<T, LocalDate?>,
    header: String = "",
    aligment: HorizontalTextAlignment = RIGHT,
    width: Int = -1,
    pattern: String = "dd/MM/yyyy",
    oculto: Boolean = false,
    block: TextColumnBuilder<LocalDate?>.() -> Unit = {}
  ): TextColumnBuilder<LocalDate> {
    val col = columnReport(
      dataType = localDateType,
      property = property,
      header = header,
      aligment = aligment,
      width = width,
      pattern = pattern,
      oculto = oculto,
      block = block
    )
    col.setValueFormatter(DateFormatter(pattern))
    return col
  }

  @JvmName("columnDate")
  protected fun columnReport(
    property: KProperty1<T, Date>,
    header: String = "",
    aligment: HorizontalTextAlignment = RIGHT,
    width: Int = -1,
    pattern: String = "dd/MM/yyyy",
    oculto: Boolean = false,
    block: TextColumnBuilder<Date?>.() -> Unit = {}
  ): TextColumnBuilder<Date> {
    return columnReport(
      dataType = type.dateDayType(),
      property = property,
      header = header,
      aligment = aligment,
      width = width,
      pattern = pattern,
      oculto = oculto,
      block = block
    )
  }

  private fun columnBuilder(): List<TextColumnBuilder<out Any>> {
    return columnsList
  }

  protected open fun titleBuider(propriedades: PropriedadeRelatorio): ComponentBuilder<*, *> {
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

  private fun pageFooterBuilder(): ComponentBuilder<*, *>? {
    return cmp.verticalList()
  }

  private fun subtotalBuilder(): List<SubtotalBuilder<*, *>> {
    return emptyList()
  }

  open fun makeReport(itens: List<T>): JasperReportBuilder {
    val labelTitleCol = labelTitleCol()
    val itemGroup = if (labelTitleCol == null) null
    else grp.group(labelTitleCol).setTitleWidth(0).setHeaderLayout(GroupHeaderLayout.VALUE)
      .showColumnHeaderAndFooter()
    val colunms = columnBuilder().toTypedArray()
    val propriedades = config(itens)

    return report()
      .title(titleBuider(propriedades))
      .setTemplate(Templates.reportTemplate)
      .columns(* colunms)
      .columnGrid(* colunms)
      .setDataSource(itens)
      .setPageFormat(propriedades.pageType, propriedades.pageOrientation)
      .setPageMargin(margin(28))
      .summary(pageFooterBuilder())
      .subtotalsAtSummary(* subtotalBuilder().toTypedArray())
      .setSubtotalStyle(stl.style().setPadding(2).setTopBorder(stl.pen1Point()))
      .pageFooter(cmp.pageNumber().setHorizontalTextAlignment(RIGHT).setStyle(stl.style().setFontSize(8)))
      .setColumnStyle(
        stl.style()
          .setFontSize(propriedades.detailFonteSize)
          .setLeftPadding(4)
          .setRightPadding(4)
          .setTopPadding(2)
          .setBottomPadding(2)
          .setBottomBorder(stl.pen1Point())
      )
      .setDetailStyle(stl.style().setFontSize(propriedades.detailFonteSize))
      .apply {
        if (itemGroup != null) this.groupBy(itemGroup).addGroupFooter(itemGroup, cmp.text(""))
          .setShowColumnTitle(false)
      }
  }

  fun processaRelatorio(itens: List<T>): ByteArray {
    val report = makeReport(itens)
    val printList = listOf(report.toJasperPrint())
    return renderReport(printList)
  }

  private fun renderReport(printList: List<JasperPrint>): ByteArray {
    val exporter = JRPdfExporter()
    val out = ByteArrayOutputStream()
    exporter.setExporterInput(SimpleExporterInput.getInstance(printList))

    exporter.exporterOutput = SimpleOutputStreamExporterOutput(out)

    exporter.exportReport()
    return out.toByteArray()
  }

  protected fun TextColumnBuilder<*>.scaleFont() {
    this.setTextAdjust(TextAdjust.SCALE_FONT)
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
    return value?.format(DateTimeFormatter.ofPattern(pattern))
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
}

private class DateFormatter(private val pattern: String) : AbstractValueFormatter<String?, LocalDate?>() {
  override fun format(value: LocalDate?, reportParameters: ReportParameters?): String {
    return value.format(pattern)
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