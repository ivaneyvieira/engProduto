package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.ProdutoEstoque

class PlanilhaProdutoEstoque : Planilha<ProdutoEstoque>("Estoque") {
  init {
    columnSheet(ProdutoEstoque::codigo, header = "Código")
    columnSheet(ProdutoEstoque::descricao, header = "Descrição")
    columnSheet(ProdutoEstoque::grade, header = "Grade")

    columnSheet(ProdutoEstoque::saldoSistema, header = "Sistema")
    columnSheet(ProdutoEstoque::qtdDif, header = "Loja")
    columnSheet(ProdutoEstoque::kardec, header = "CD")
    columnSheet(ProdutoEstoque::quantDevolucao, header = "Gar")
  }
}