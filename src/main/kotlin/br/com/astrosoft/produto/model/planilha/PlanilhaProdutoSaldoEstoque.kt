package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoSaldoEstoque

class PlanilhaProdutoSaldoEstoque : Planilha<ProdutoSaldoEstoque>("Produto") {
  init {
    columnSheet(ProdutoSaldoEstoque::loja, header = "Loja")
    columnSheet(ProdutoSaldoEstoque::codigo, header = "Código")
    columnSheet(ProdutoSaldoEstoque::descricao, header = "Descrição")
    columnSheet(ProdutoSaldoEstoque::gradeProduto, header = "Grade")
    columnSheet(ProdutoSaldoEstoque::unidade, header = "Unidade")
    columnSheet(ProdutoSaldoEstoque::qttyVarejo, header = "Varejo")
    columnSheet(ProdutoSaldoEstoque::qttyAtacado, header = "Atacado")
    columnSheet(ProdutoSaldoEstoque::qttyTotal, header = "Total")
    columnSheet(ProdutoSaldoEstoque::custoVarejo, pattern = "0.0000", header = "Custo Med")
    columnSheet(ProdutoSaldoEstoque::custoTotal, pattern = "0.00", header = "Custo Total")
    columnSheet(ProdutoSaldoEstoque::tributacao, header = "Trib")
    columnSheet(ProdutoSaldoEstoque::rotulo, header = "Rotulo")
    columnSheet(ProdutoSaldoEstoque::ncm, header = "NCM")
    columnSheet(ProdutoSaldoEstoque::fornecedor, header = "For")
    columnSheet(ProdutoSaldoEstoque::abrev, header = "Abrev")
    columnSheet(ProdutoSaldoEstoque::tipo, header = "Tipo")
    columnSheet(ProdutoSaldoEstoque::cl, header = "Cl")
  }
}