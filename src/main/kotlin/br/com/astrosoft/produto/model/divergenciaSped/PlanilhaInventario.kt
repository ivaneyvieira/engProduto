package br.com.astrosoft.produto.model.divergenciaSped

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.math.BigDecimal

data class PlanilhaInventario(
  val numeroProduto: String,
  val descricaoProduto: String,
  val quantidade: BigDecimal,
  val custoMedio: BigDecimal,
  val valorTotal: BigDecimal
)

private val CABECALHOS_PADRAO = listOf(
  "No. Produto",
  "Descricao do Produto",
  "Quantidade",
  "Custo Medio",
  "Valor Total"
)

fun lerPlanilhaInventario(
  caminhoArquivo: String,
  nomeAba: String = "Relatório",
  cabecalhosEsperados: List<String>? = CABECALHOS_PADRAO
): List<PlanilhaInventario> {
  val formatter = DataFormatter()

  File(caminhoArquivo).inputStream().use { input ->
    WorkbookFactory.create(input).use { workbook ->
      val sheet = workbook.getSheet(nomeAba) ?: workbook.getSheetAt(0)
      val evaluator = workbook.creationHelper.createFormulaEvaluator()

      validarCabecalho(sheet, formatter, cabecalhosEsperados)

      val produtos = mutableListOf<PlanilhaInventario>()

      for (rowIndex in 1..sheet.lastRowNum) { // comeca da linha 2, ignorando o cabecalho
        val row = sheet.getRow(rowIndex) ?: continue

        val numeroProduto = getString(row, 0, formatter)
        val descricaoProduto = getString(row, 1, formatter)
        val quantidade = getBigDecimal(row, 2, formatter, evaluator)
        val custoMedio = getBigDecimal(row, 3, formatter, evaluator)
        val valorTotal = getBigDecimal(row, 4, formatter, evaluator)

        // ignora linhas completamente vazias
        if (
          numeroProduto.isBlank() &&
          descricaoProduto.isBlank() &&
          isZero(quantidade) &&
          isZero(custoMedio) &&
          isZero(valorTotal)
        ) {
          continue
        }

        produtos.add(
          PlanilhaInventario(
            numeroProduto = numeroProduto,
            descricaoProduto = descricaoProduto,
            quantidade = quantidade,
            custoMedio = custoMedio,
            valorTotal = valorTotal
          )
        )
      }

      return produtos
    }
  }
}

private fun validarCabecalho(
  sheet: Sheet,
  formatter: DataFormatter,
  cabecalhosEsperados: List<String>?
) {
  if (cabecalhosEsperados.isNullOrEmpty()) return

  val headerRow = sheet.getRow(0)
  val encontrados = cabecalhosEsperados.indices.map { index ->
    headerRow?.let { getString(it, index, formatter) } ?: ""
  }

  val esperadoNormalizado = cabecalhosEsperados.map { it.trim().lowercase() }
  val encontradoNormalizado = encontrados.map { it.trim().lowercase() }

  if (encontradoNormalizado != esperadoNormalizado) {
    throw IllegalArgumentException(
      "Cabecalho invalido na planilha. Esperado: ${cabecalhosEsperados.joinToString()} " +
        "Encontrado: ${encontrados.joinToString()}"
    )
  }
}

private fun getString(row: Row, cellIndex: Int, formatter: DataFormatter): String {
  val cell = row.getCell(cellIndex) ?: return ""
  return formatter.formatCellValue(cell).trim()
}

private fun getBigDecimal(
  row: Row,
  cellIndex: Int,
  formatter: DataFormatter,
  evaluator: FormulaEvaluator
): BigDecimal {
  val cell = row.getCell(cellIndex) ?: return BigDecimal.ZERO

  return when (cell.cellType) {
    CellType.NUMERIC -> BigDecimal.valueOf(cell.numericCellValue)
    CellType.STRING  -> parseDecimalOrZero(formatter.formatCellValue(cell))
    CellType.FORMULA -> parseDecimalOrZero(formatter.formatCellValue(cell, evaluator))
    else             -> BigDecimal.ZERO
  }
}

private fun isZero(value: BigDecimal): Boolean {
  return value.compareTo(BigDecimal.ZERO) == 0
}
