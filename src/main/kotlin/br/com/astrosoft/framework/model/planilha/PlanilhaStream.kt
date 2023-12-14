package br.com.astrosoft.framework.model.planilha

import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.streaming.SXSSFSheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KProperty1

open class PlanilhaStream<B>(private val sheatName: String) {
  private val styles: MutableMap<String, XSSFCellStyle> = mutableMapOf()
  protected val columns: MutableList<Column<B, *>> = mutableListOf()

  @JvmName("campoString")
  fun columnSheet(property: KProperty1<B, String?>, header: String) {
    val campo = Column<B, String>(header = header, pattern = null) {
      property.get(it) ?: ""
    }
    columns.add(campo)
  }

  @JvmName("campoInt")
  fun columnSheet(property: KProperty1<B, Int?>, header: String, pattern: String? = "#,##0") {
    val campo = Column<B, Int>(header = header, pattern = pattern) {
      property.get(it) ?: 0
    }
    columns.add(campo)
  }

  @JvmName("campoDouble")
  fun columnSheet(property: KProperty1<B, Double?>, header: String, pattern: String? = "#,##0.00") {
    val campo = Column<B, Double>(header = header, pattern = pattern) {
      property.get(it) ?: 0.00
    }
    columns.add(campo)
  }

  @JvmName("campoDate")
  fun columnSheet(property: KProperty1<B, Date?>, header: String, pattern: String? = "dd/mm/yyyy") {
    val campo = Column<B, Date?>(header = header, pattern = pattern) {
      property.get(it)
    }
    columns.add(campo)
  }

  @JvmName("campoLocalDate")
  fun columnSheet(property: KProperty1<B, LocalDate?>, header: String, pattern: String? = "dd/mm/yyyy") {
    val campo = Column<B, LocalDate?>(header = header, pattern = pattern) {
      property.get(it)
    }
    columns.add(campo)
  }

  private fun SXSSFSheet.row(bean: B) {
    val row = this.createRow(this.physicalNumberOfRows)
    val creationHelper = workbook.creationHelper

    columns.forEachIndexed { index, column ->
      val cellValue = column.property.value(bean)
      //autoSizeColumn(index)
      row.createCell(index).apply {
        when (cellValue) {
          is String    -> setCellValue(cellValue)

          is Int       -> {
            val style = workbook.createCellStyle()
            style.dataFormat = creationHelper.createDataFormat().getFormat(column.pattern ?: "#,##0")
            cellStyle = style
            setCellValue(cellValue.toDouble())
          }

          is Number    -> {
            val style = workbook.createCellStyle()
            style.dataFormat = creationHelper.createDataFormat().getFormat(column.pattern ?: "#,##0.00")
            cellStyle = style
            setCellValue(cellValue.toDouble())
          }

          is Date      -> {
            val style = workbook.createCellStyle()
            style.dataFormat = creationHelper.createDataFormat().getFormat(column.pattern ?: "dd/mm/yyyy")
            cellStyle = style
            setCellValue(cellValue)
          }

          is LocalDate -> {
            val style = workbook.createCellStyle()
            style.dataFormat = creationHelper.createDataFormat().getFormat(column.pattern ?: "dd/mm/yyyy")
            cellStyle = style
            setCellValue(cellValue)
          }

          else         -> {
            if (cellValue != null)
              setCellValue(cellValue.toString())
            else
              setCellValue("")
          }
        }
      }
    }
  }

  fun SXSSFWorkbook.cellStyle(name: String, block: CellStyle.() -> Unit = {}): XSSFCellStyle =
      styles.computeIfAbsent(name) { this.createCellStyle() as XSSFCellStyle }
        .apply(block)

  private fun SXSSFWorkbook.cloneAndFormat(style: CellStyle?, format: Short) = this.createCellStyle().apply {
    if (style != null) {
      cloneStyleFrom(style)
    }
    dataFormat = format
  }

  private fun SXSSFSheet.row(cells: List<Any>, wb: SXSSFWorkbook, style: CellStyle? = null): List<Cell> {
    val format = wb.createDataFormat()
    val dateFormat = format.getFormat("MM/DD/YYYY")
    val timeStampFormat = format.getFormat("MM/DD/YYYY HH:MM:SS")
    val calendarFormat = format.getFormat("MMM D, YYYY")

    val cellList = mutableListOf<Cell>()
    val row = this.createRow(this.physicalNumberOfRows)
    var col = 0
    cells.forEach { cellValue ->
      cellList.add(row.createCell(col++).apply {
        cellStyle = style
        when (cellValue) {
          is String        -> setCellValue(cellValue)
          is Number        -> setCellValue(cellValue.toDouble())
          is LocalDateTime -> {
            cellStyle = wb.cloneAndFormat(style, timeStampFormat)
            setCellValue(cellValue)
          }

          is LocalDate     -> {
            cellStyle = wb.cloneAndFormat(style, dateFormat)
            setCellValue(cellValue)
          }

          is Calendar      -> {
            cellStyle = wb.cloneAndFormat(style, calendarFormat)
            setCellValue(cellValue)
          }

          else             -> setCellValue(cellValue.toString())
        }
      })
    }
    return cellList
  }

  fun write(listBean: List<B>): ByteArray {
    val wb = SXSSFWorkbook().apply {
      val wb = this
      val headerStyle = cellStyle("Header") {
        fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        fillPattern = FillPatternType.SOLID_FOREGROUND
        this.verticalAlignment = VerticalAlignment.TOP
      }

      val stNotas = this.createSheet(sheatName).apply {
        val headers = columns.map { it.header }
        row(headers, wb, headerStyle)
        val listTotal = listBean.size

        listBean.forEachIndexed { index, bean ->
          println("planilha $index/$listTotal")
          row(bean)
        }
      }

     // columns.forEachIndexed { index, _ ->
     //   stNotas.autoSizeColumn(index)
     // }
    }
    val outBytes = ByteArrayOutputStream()
    wb.write(outBytes)
    return outBytes.toByteArray()
  }
}

