package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoInventario

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
    columnSheet(ProdutoInventario::tipoStr, header = "Tipo")
    columnSheet(ProdutoInventario::movimento, header = "Est")
    columnSheet(ProdutoInventario::saida, header = "Saída")
    columnSheet(ProdutoInventario::vencimentoStr, header = "Venc")
  }
}