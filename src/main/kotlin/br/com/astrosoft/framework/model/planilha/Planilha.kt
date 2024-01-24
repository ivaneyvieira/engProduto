package br.com.astrosoft.framework.model.planilha

import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.reflect.KProperty1

open class Planilha<B>(private val sheatName: String) {
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

  @JvmName("campoLocalTime")
  fun columnSheet(property: KProperty1<B, LocalTime?>, header: String, pattern: String? = "hh:MM") {
    val campo = Column<B, LocalTime?>(header = header, pattern = pattern) {
      property.get(it)
    }
    columns.add(campo)
  }

  private fun Sheet.row(bean: B) {
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

          is LocalDateTime -> {
            val style = workbook.createCellStyle()
            style.dataFormat = creationHelper.createDataFormat().getFormat(column.pattern ?: "hh:mm")
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

  fun write(listBean: List<B>): ByteArray {
    val wb = XSSFWorkbook()
    val headerStyle = wb.createCellStyle().apply {
      fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
      fillPattern = FillPatternType.SOLID_FOREGROUND
      verticalAlignment = VerticalAlignment.TOP
    }

    val stNotas = wb.createSheet(sheatName).apply {
      val headerRow = this.createRow(0)
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
}

