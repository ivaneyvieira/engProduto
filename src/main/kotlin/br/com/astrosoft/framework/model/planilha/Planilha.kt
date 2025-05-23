package br.com.astrosoft.framework.model.planilha

import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.reflect.KProperty1

open class Planilha<B>(private val sheatName: String) {
  protected var headerStyle: XSSFCellStyle? = null
  protected val columns: MutableList<Column<B, *>> = mutableListOf()

  private val mapStyles = mutableMapOf<String, CellStyle>()

  private fun Workbook.createStyle(pattern: String): CellStyle {
    return mapStyles.getOrPut(pattern) {
      val style = this.createCellStyle()
      style.dataFormat = this.creationHelper.createDataFormat().getFormat(pattern)
      style
    }
  }

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

  @JvmName("campoLocalTime")
  fun columnSheet(property: KProperty1<B, LocalTime?>, header: String, pattern: String? = "hh:MM") {
    val campo = Column<B, LocalTime?>(header = header, pattern = pattern) {
      property.get(it)
    }
    columns.add(campo)
  }

  private fun Sheet.row(bean: B) {
    val row = this.createRow(this.physicalNumberOfRows)

    columns.forEachIndexed { index, column ->
      val cellValue = column.property.value(bean)

      row.createCell(index).apply {
        when (cellValue) {
          is String -> setCellValue(cellValue)

          is Int -> {
            cellStyle = workbook.createStyle("#,##0")
            setCellValue(cellValue.toDouble())
          }

          is Number -> {
            cellStyle = workbook.createStyle("#,##0.00")
            setCellValue(cellValue.toDouble())
          }

          is Date -> {
            cellStyle = workbook.createStyle("dd/mm/yyyy")
            setCellValue(cellValue)
          }

          is LocalDate -> {
            cellStyle = workbook.createStyle("dd/mm/yyyy")
            setCellValue(cellValue)
          }

          is LocalDateTime -> {
            cellStyle = workbook.createStyle("hh:MM")
            setCellValue(cellValue)
          }

          else -> {
            if (cellValue != null)
              setCellValue(cellValue.toString())
            else
              setCellValue("")
          }
        }
      }
    }
  }

  fun write(listBean: List<B>): ByteArray {
    val wb = XSSFWorkbook()
    headerStyle = wb.createCellStyle().apply {
      fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
      fillPattern = FillPatternType.SOLID_FOREGROUND
      verticalAlignment = VerticalAlignment.TOP
      this.borderTop = BorderStyle.THIN
      this.borderBottom = BorderStyle.THIN
      this.borderLeft = BorderStyle.THIN
      this.borderRight = BorderStyle.THIN
    }

    val stNotas = wb.createSheet(sheatName).apply {
      this.beforeWrite()
      val headerRow = this.createRow(this.physicalNumberOfRows)
      val headers = columns.map { it.header }
      headers.forEachIndexed { index, header ->
        headerRow.createCell(index).apply {
          setCellValue(header)
          cellStyle = headerStyle
        }
      }

      val listTotal = listBean.size

      listBean.forEachIndexed { index, bean ->
        println("planilha $index/$listTotal")
        row(bean)
      }
    }

    columns.forEachIndexed { index, _ ->
      stNotas.autoSizeColumn(index)
    }

    val outBytes = ByteArrayOutputStream()
    wb.write(outBytes)
    return outBytes.toByteArray()
  }

  protected open fun XSSFSheet.beforeWrite() {
  }
}



