package br.com.astrosoft.produto.model.beans

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.math.BigDecimal

data class PlanilhaInventario(
  val numeroProduto: String,
  val descricaoProduto: String,
  val quantidade: Double,
  val custoMedio: Double,
  val valorTotal: Double
)

fun lerPlanilhaInventario(caminhoArquivo: String): List<PlanilhaInventario> {
  val formatter = DataFormatter()

  File(caminhoArquivo).inputStream().use { input ->
    val workbook = WorkbookFactory.create(input)
    val sheet = workbook.getSheet("Relatório") ?: workbook.getSheetAt(0)

    val produtos = mutableListOf<PlanilhaInventario>()

    for (rowIndex in 1..sheet.lastRowNum) { // começa da linha 2, ignorando o cabeçalho
      val row = sheet.getRow(rowIndex) ?: continue

      val numeroProduto = getString(row, 0, formatter)
      val descricaoProduto = getString(row, 1, formatter)
      val quantidade = getBigDecimal(row, 2, formatter)
      val custoMedio = getBigDecimal(row, 3, formatter)
      val valorTotal = getBigDecimal(row, 4, formatter)

      // ignora linhas completamente vazias
      if (
        numeroProduto.isBlank() &&
        descricaoProduto.isBlank() &&
        quantidade == BigDecimal.ZERO &&
        custoMedio == BigDecimal.ZERO &&
        valorTotal == BigDecimal.ZERO
      ) {
        continue
      }

      produtos.add(
        PlanilhaInventario(
          numeroProduto = numeroProduto,
          descricaoProduto = descricaoProduto,
          quantidade = quantidade.toDouble(),
          custoMedio = custoMedio.toDouble(),
          valorTotal = valorTotal.toDouble()
        )
      )
    }

    workbook.close()
    return produtos
  }
}

private fun getString(row: Row, cellIndex: Int, formatter: DataFormatter): String {
  val cell = row.getCell(cellIndex) ?: return ""
  return formatter.formatCellValue(cell).trim()
}

private fun getBigDecimal(row: Row, cellIndex: Int, formatter: DataFormatter): BigDecimal {
  val cell = row.getCell(cellIndex) ?: return BigDecimal.ZERO

  return when (cell.cellType) {
    CellType.NUMERIC -> BigDecimal.valueOf(cell.numericCellValue)
    CellType.STRING  -> {
      val texto = formatter.formatCellValue(cell)
        .replace(".", "")
        .replace(",", ".")
        .trim()

      texto.toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    CellType.FORMULA -> {
      try {
        BigDecimal.valueOf(cell.numericCellValue)
      } catch (_: Exception) {
        val texto = formatter.formatCellValue(cell)
          .replace(".", "")
          .replace(",", ".")
          .trim()

        texto.toBigDecimalOrNull() ?: BigDecimal.ZERO
      }
    }

    else             -> BigDecimal.ZERO
  }
}