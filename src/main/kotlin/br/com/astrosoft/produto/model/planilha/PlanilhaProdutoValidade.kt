package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoValidade
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet

class PlanilhaProdutoValidade : Planilha<ProdutoValidade>("Validade Inventário") {
  init {
    columnSheet(ProdutoValidade::codigo, header = "Código")
    columnSheet(ProdutoValidade::descricao, header = "Descrição")
    columnSheet(ProdutoValidade::grade, header = "Grade")
    columnSheet(ProdutoValidade::unidade, header = "Un")
    columnSheet(ProdutoValidade::validade, header = "Val")
    columnSheet(ProdutoValidade::estoqueTotal, header = "Total")
    columnSheet(ProdutoValidade::estoqueDS, header = "Est")
    columnSheet(ProdutoValidade::vencimentoDS, header = "Venc", pattern = "mm/yy")
    columnSheet(ProdutoValidade::estoqueMR, header = "Est")
    columnSheet(ProdutoValidade::vencimentoMR, header = "Venc", pattern = "mm/yy")
    columnSheet(ProdutoValidade::estoqueMF, header = "Est")
    columnSheet(ProdutoValidade::vencimentoMF, header = "Venc", pattern = "mm/yy")
    columnSheet(ProdutoValidade::estoquePK, header = "Est")
    columnSheet(ProdutoValidade::vencimentoPK, header = "Venc", pattern = "mm/yy")
    columnSheet(ProdutoValidade::estoqueTM, header = "Est")
    columnSheet(ProdutoValidade::vencimentoTM, header = "Venc", pattern = "mm/yy")
  }

  override fun XSSFSheet.beforeWrite() {
    val numRow = this.physicalNumberOfRows
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 0, 5))
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 6, 7))
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 8, 9))
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 10, 11))
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 12, 13))
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 14, 15))
    val headerRow = this.createRow(numRow)
    headerRow.createCell(0).apply {
      setCellValue("Produto")
      cellStyle = headerStyle
    }
    headerRow.createCell(6).apply {
      setCellValue("DS")
      cellStyle = headerStyle
    }
    headerRow.createCell(8).apply {
      setCellValue("MR")
      cellStyle = headerStyle
    }
    headerRow.createCell(10).apply {
      setCellValue("MF")
      cellStyle = headerStyle
    }
    headerRow.createCell(12).apply {
      setCellValue("PK")
      cellStyle = headerStyle
    }
    headerRow.createCell(14).apply {
      setCellValue("TM")
      cellStyle = headerStyle
    }
  }
}