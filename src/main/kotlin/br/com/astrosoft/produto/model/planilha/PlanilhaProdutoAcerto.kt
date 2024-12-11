package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoAcerto

class PlanilhaProdutoAcerto : Planilha<ProdutoAcerto>("ProdutoAcerto") {
  init {
    columnSheet(ProdutoAcerto::pedido, header = "Pedido")
    columnSheet(ProdutoAcerto::codigo, header = "Código")
    columnSheet(ProdutoAcerto::barcode, header = "Código de Barras")
    columnSheet(ProdutoAcerto::descricao, header = "Descrição")
    columnSheet(ProdutoAcerto::grade, header = "Grade")
    columnSheet(ProdutoAcerto::localizacao, header = "Loc App")
    columnSheet(ProdutoAcerto::validade, header = "Val")
    columnSheet(ProdutoAcerto::qtPedido, header = "Quant")
    columnSheet(ProdutoAcerto::estoque, header = "Estoque")
  }
}