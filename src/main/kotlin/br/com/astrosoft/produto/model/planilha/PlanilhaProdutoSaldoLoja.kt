package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoLoja

class PlanilhaProdutoSaldoLoja : Planilha<ProdutoLoja>("Produto") {
  init {
    columnSheet(ProdutoLoja::codigo, header = "Código")
    columnSheet(ProdutoLoja::descricao, header = "Descrição")
    columnSheet(ProdutoLoja::gradeProduto, header = "Grade")
    columnSheet(ProdutoLoja::unidade, header = "Unidade")
    columnSheet(ProdutoLoja::estoqueTotal, header = "Est Total")
    columnSheet(ProdutoLoja::estoqueDS, header = "DS")
    columnSheet(ProdutoLoja::estoqueMR, header = "MR")
    columnSheet(ProdutoLoja::estoqueMF, header = "MF")
    columnSheet(ProdutoLoja::estoquePK, header = "PK")
    columnSheet(ProdutoLoja::estoqueTM, header = "TM")
    columnSheet(ProdutoLoja::ncm, header = "NCM")
    columnSheet(ProdutoLoja::fornecedor, header = "For")
    columnSheet(ProdutoLoja::tipo, header = "Tipo")
    columnSheet(ProdutoLoja::cl, header = "Cl")
  }
}