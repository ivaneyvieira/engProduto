package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoInventario
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet

class PlanilhaProdutoInventario : Planilha<ProdutoInventario>("Validade Inventário") {
  init {
    columnSheet(ProdutoInventario::codigo, header = "Código")
    columnSheet(ProdutoInventario::descricao, header = "Descrição")
    columnSheet(ProdutoInventario::grade, header = "Grade")
    columnSheet(ProdutoInventario::unidade, header = "Un")
    columnSheet(ProdutoInventario::vendno, header = "Cod For")
    columnSheet(ProdutoInventario::fornecedorAbrev, header = "Fornecedor")
    columnSheet(ProdutoInventario::validade, header = "Val")
    columnSheet(ProdutoInventario::estoqueTotal, header = "Total")
    columnSheet(ProdutoInventario::estoqueDS, header = "Est")
    columnSheet(ProdutoInventario::vendasDS, header = "Saída")
    columnSheet(ProdutoInventario::vencimentoDSStr, header = "Venc")
    columnSheet(ProdutoInventario::estoqueMR, header = "Est")
    columnSheet(ProdutoInventario::vendasMR, header = "Saída")
    columnSheet(ProdutoInventario::vencimentoMRStr, header = "Venc")
    columnSheet(ProdutoInventario::estoqueMF, header = "Est")
    columnSheet(ProdutoInventario::vendasMF, header = "Saída")
    columnSheet(ProdutoInventario::vencimentoMFStr, header = "Venc")
    columnSheet(ProdutoInventario::estoquePK, header = "Est")
    columnSheet(ProdutoInventario::vendasPK, header = "Saída")
    columnSheet(ProdutoInventario::vencimentoPKStr, header = "Venc")
    columnSheet(ProdutoInventario::estoqueTM, header = "Est")
    columnSheet(ProdutoInventario::vendasTM, header = "Saída")
    columnSheet(ProdutoInventario::vencimentoTMStr, header = "Venc")
  }

  override fun XSSFSheet.beforeWrite() {
    val numRow = this.physicalNumberOfRows
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 0, 7))
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 8, 10))
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 11, 13))
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 14, 16))
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 17, 19))
    this.addMergedRegion(CellRangeAddress(numRow, numRow, 20, 22))
    val headerRow = this.createRow(numRow)
    headerRow.createCell(0).apply {
      setCellValue("Produto")
      cellStyle = headerStyle
    }
    headerRow.createCell(8).apply {
      setCellValue("DS")
      cellStyle = headerStyle
    }
    headerRow.createCell(11).apply {
      setCellValue("MR")
      cellStyle = headerStyle
    }
    headerRow.createCell(14).apply {
      setCellValue("MF")
      cellStyle = headerStyle
    }
    headerRow.createCell(17).apply {
      setCellValue("PK")
      cellStyle = headerStyle
    }
    headerRow.createCell(20).apply {
      setCellValue("TM")
      cellStyle = headerStyle
    }
  }
}