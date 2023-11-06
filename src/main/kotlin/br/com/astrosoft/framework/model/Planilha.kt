package br.com.astrosoft.framework.model

import br.com.astrosoft.framework.util.toDate
import com.github.nwillc.poink.PSheet
import com.github.nwillc.poink.workbook
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.*
import kotlin.reflect.KProperty1

fun interface ProduceValue<B, T : Any?> {
  fun value(bean: B): T
}

class Column<B, T : Any?>(val header: String, val pattern: String?, val property: ProduceValue<B, T>)
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

  private fun PSheet.row(bean: B) {
    val row = this.createRow(this.physicalNumberOfRows)
    val creationHelper = workbook.creationHelper

    columns.forEachIndexed { index, column ->
      val cellValue = column.property.value(bean)
      autoSizeColumn(index)
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

  fun write(listBean: List<B>): ByteArray {
    val wb = workbook {
      val headerStyle = cellStyle("Header") {
        fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        fillPattern = FillPatternType.SOLID_FOREGROUND
        this.verticalAlignment = VerticalAlignment.TOP
      }

      val stNotas = sheet(sheatName) {
        val headers = columns.map { it.header }
        row(headers, headerStyle)
        listBean.forEach { bean ->
          row(bean)
        }
      }

      columns.forEachIndexed { index, _ ->
        stNotas.autoSizeColumn(index)
      }
    }
    val outBytes = ByteArrayOutputStream()
    wb.write(outBytes)
    return outBytes.toByteArray()
  }
}