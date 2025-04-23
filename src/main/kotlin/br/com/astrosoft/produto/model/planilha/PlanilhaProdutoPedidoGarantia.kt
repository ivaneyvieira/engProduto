package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoPedidoGarantia

class PlanilhaProdutoPedidoGarantia : Planilha<ProdutoPedidoGarantia>("Estoque") {
  init {
    columnSheet(ProdutoPedidoGarantia::lojaReceb, header = "Loja")
    columnSheet(ProdutoPedidoGarantia::niReceb, header = "NI")
    columnSheet(ProdutoPedidoGarantia::nfoReceb, header = "NFO")
    columnSheet(ProdutoPedidoGarantia::entradaReceb, header = "Entrada")
    columnSheet(ProdutoPedidoGarantia::cfopReceb, header = "CFOP")
    columnSheet(ProdutoPedidoGarantia::forReceb, header = "For NFD")

    columnSheet(ProdutoPedidoGarantia::ref, header = "Ref Fab")
    columnSheet(ProdutoPedidoGarantia::loteDev, header = "Lote")
    columnSheet(ProdutoPedidoGarantia::codigo, header = "Código")
    columnSheet(ProdutoPedidoGarantia::descricao, header = "Descrição")
    columnSheet(ProdutoPedidoGarantia::grade, header = "Grade")

    columnSheet(ProdutoPedidoGarantia::estoqueDev, header = "Est Dev")
    columnSheet(ProdutoPedidoGarantia::valorUnitario, "V. Unit")
    columnSheet(ProdutoPedidoGarantia::valorTotal, "V. Total")
  }
}