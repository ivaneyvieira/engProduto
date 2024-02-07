package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoSaldo

class PlanilhaProdutoSaldo : Planilha<ProdutoSaldo>("Produto") {
  init {
    columnSheet(ProdutoSaldo::loja, header = "Loja")
    columnSheet(ProdutoSaldo::codigo, header = "Código")
    columnSheet(ProdutoSaldo::descricao, header = "Descrição")
    columnSheet(ProdutoSaldo::gradeProduto, header = "Grade")
    columnSheet(ProdutoSaldo::unidade, header = "Unidade")
    columnSheet(ProdutoSaldo::estoqueLojas, header = "Est Lojas")
    columnSheet(ProdutoSaldo::qttyVarejo, header = "Varejo")
    columnSheet(ProdutoSaldo::qttyAtacado, header = "Atacado")
    columnSheet(ProdutoSaldo::qttyTotal, header = "Total")
    columnSheet(ProdutoSaldo::tributacao, header = "Trib")
    columnSheet(ProdutoSaldo::rotulo, header = "Rotulo")
    columnSheet(ProdutoSaldo::ncm, header = "NCM")
    columnSheet(ProdutoSaldo::fornecedor, header = "For")
    columnSheet(ProdutoSaldo::abrev, header = "Abrev")
    columnSheet(ProdutoSaldo::tipo, header = "Tipo")
    columnSheet(ProdutoSaldo::cl, header = "Cl")
  }
}