package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoEstoque

class PlanilhaProdutoEstoque : Planilha<ProdutoEstoque>("Estoque") {
  init {
    columnSheet(ProdutoEstoque::codigo, header = "Código")
    columnSheet(ProdutoEstoque::descricao, header = "Descrição")
    columnSheet(ProdutoEstoque::grade, header = "Grade")
    columnSheet(ProdutoEstoque::unidade, header = "UN")
    columnSheet(ProdutoEstoque::saldo, header = "Estoque")
    columnSheet(ProdutoEstoque::qtdEmbalagem, header = "Emb")
    columnSheet(ProdutoEstoque::kardec, header = "Kardec")
    columnSheet(ProdutoEstoque::kardecEmb, header = "Kardec Emb")
  }
}