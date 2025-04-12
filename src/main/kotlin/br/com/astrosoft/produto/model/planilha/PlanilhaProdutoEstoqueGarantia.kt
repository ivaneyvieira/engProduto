package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueGarantia

class PlanilhaProdutoEstoqueGarantia : Planilha<ProdutoEstoqueGarantia>("Estoque") {
  init {
    columnSheet(ProdutoEstoqueGarantia::numloja, header = "Num Loja")
    columnSheet(ProdutoEstoqueGarantia::lojaSigla, header = "Loja")
    columnSheet(ProdutoEstoqueGarantia::numero, header = "Número")
    columnSheet(ProdutoEstoqueGarantia::data, header = "Data")
    columnSheet(ProdutoEstoqueGarantia::hora, header = "Hora")

    columnSheet(ProdutoEstoqueGarantia::codigo, header = "Código")
    columnSheet(ProdutoEstoqueGarantia::descricao, header = "Descrição")
    columnSheet(ProdutoEstoqueGarantia::grade, header = "Grade")

    columnSheet(ProdutoEstoqueGarantia::estoqueSis, header = "Est Sist")
    columnSheet(ProdutoEstoqueGarantia::estoqueReal, header = "Est Real")
  }
}