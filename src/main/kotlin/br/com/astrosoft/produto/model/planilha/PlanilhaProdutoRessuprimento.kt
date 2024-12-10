package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento

class PlanilhaProdutoRessuprimento : Planilha<ProdutoRessuprimento>("ProdutoRessuprimento") {
  init {
    columnSheet(ProdutoRessuprimento::ordno, header = "Pedido")
    columnSheet(ProdutoRessuprimento::codigo, header = "Código")
    columnSheet(ProdutoRessuprimento::barcodes, header = "Código de Barras")
    columnSheet(ProdutoRessuprimento::descricao, header = "Descrição")
    columnSheet(ProdutoRessuprimento::grade, header = "Grade")
    columnSheet(ProdutoRessuprimento::localizacao, header = "Loc App")
    columnSheet(ProdutoRessuprimento::validade, header = "Val")
    columnSheet(ProdutoRessuprimento::qtPedido, header = "Quant")
    columnSheet(ProdutoRessuprimento::estoque, header = "Estoque")
  }
}