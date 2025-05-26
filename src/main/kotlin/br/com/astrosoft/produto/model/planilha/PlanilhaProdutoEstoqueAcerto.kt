package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto

class PlanilhaProdutoEstoqueAcerto : Planilha<ProdutoEstoqueAcerto>("Estoque") {
  init {
    columnSheet(ProdutoEstoqueAcerto::numloja, header = "Num Loja")
    columnSheet(ProdutoEstoqueAcerto::lojaSigla, header = "Loja")
    columnSheet(ProdutoEstoqueAcerto::numero, header = "Número")
    columnSheet(ProdutoEstoqueAcerto::data, header = "Data")
    columnSheet(ProdutoEstoqueAcerto::hora, header = "Hora")

    columnSheet(ProdutoEstoqueAcerto::codigo, header = "Código")
    columnSheet(ProdutoEstoqueAcerto::descricao, header = "Descrição")
    columnSheet(ProdutoEstoqueAcerto::grade, header = "Grade")

    columnSheet(ProdutoEstoqueAcerto::estoqueSis, header = "Est Sist")
    columnSheet(ProdutoEstoqueAcerto::estoqueCD, header = "Est CD")
    columnSheet(ProdutoEstoqueAcerto::estoqueLoja, header = "Est Loja")
    columnSheet(ProdutoEstoqueAcerto::diferencaAcerto, header = "Diferença")
  }
}