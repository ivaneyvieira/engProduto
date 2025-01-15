package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.Produtos

class PlanilhaProdutoLoja : Planilha<Produtos>("Produto") {
  init {
    columnSheet(Produtos::siglaLoja, header = "Loja")
    columnSheet(Produtos::codigo, header = "Código")
    columnSheet(Produtos::descricao, header = "Descrição")
    columnSheet(Produtos::grade, header = "Grade")
    columnSheet(Produtos::unidade, header = "UN")
    columnSheet(Produtos::estoque, header = "Total")
    columnSheet(Produtos::qttyVendas, header = "Venda")
    columnSheet(Produtos::mesesGarantia, header = "Val")
    columnSheet(Produtos::saldo, header = "Saldo")
    columnSheet(Produtos::qttyInv, header = "Inv")
    columnSheet(Produtos::dataVenda, header = "Data Venda")
    columnSheet(Produtos::qttyDif01, header = "QTD 1")
    columnSheet(Produtos::venc01, header = "Vence 1")
    columnSheet(Produtos::qttyDif02, header = "QTD 2")
    columnSheet(Produtos::venc02, header = "Vence 2")
    columnSheet(Produtos::qttyDif03, header = "QTD 3")
    columnSheet(Produtos::venc03, header = "Vence 3")
    columnSheet(Produtos::qttyDif04, header = "QTD 4")
    columnSheet(Produtos::venc04, header = "Vence 4")
    columnSheet(Produtos::forn, header = "Forn")
    columnSheet(Produtos::abrev, header = "Abrev")
  }
}