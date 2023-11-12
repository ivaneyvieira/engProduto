package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoTransfRessu4

class PlanilhaRessu4 : Planilha<ProdutoTransfRessu4>("Ressuprimento4") {
  init {
    columnSheet(ProdutoTransfRessu4::codigo, "Código")
    columnSheet(ProdutoTransfRessu4::descricao, "Descrição")
    columnSheet(ProdutoTransfRessu4::grade, "Grade")
    columnSheet(ProdutoTransfRessu4::codigoBarras, "Código de Barras")
    columnSheet(ProdutoTransfRessu4::referencia, "Ref Fornecedor")
    columnSheet(ProdutoTransfRessu4::quant, "Quant", pattern = "#,##0")
  }
}