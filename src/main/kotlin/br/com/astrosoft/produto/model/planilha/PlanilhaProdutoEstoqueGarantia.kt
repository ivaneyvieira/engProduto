package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueGarantia

class PlanilhaProdutoEstoqueGarantia : Planilha<ProdutoEstoqueGarantia>("Estoque") {
  init {
    columnSheet(ProdutoEstoqueGarantia::lojaReceb, header = "Loja")
    columnSheet(ProdutoEstoqueGarantia::niReceb, header = "NI")
    columnSheet(ProdutoEstoqueGarantia::nfoReceb, header = "NFO")
    columnSheet(ProdutoEstoqueGarantia::entradaReceb, header = "Entrada")
    columnSheet(ProdutoEstoqueGarantia::forReceb, header = "For NFD")

    columnSheet(ProdutoEstoqueGarantia::ref, header = "Ref Fab")
    columnSheet(ProdutoEstoqueGarantia::codigo, header = "Código")
    columnSheet(ProdutoEstoqueGarantia::descricao, header = "Descrição")
    columnSheet(ProdutoEstoqueGarantia::grade, header = "Grade")

    columnSheet(ProdutoEstoqueGarantia::estoqueDev, header = "Est Dev")
  }
}