package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.Produtos

class PlanilhaProduto : Planilha<Produtos>("Produto") {
  init {
    columnSheet(Produtos::codigo, header = "Cód")
    columnSheet(Produtos::descricao, header = "Descrição")
    columnSheet(Produtos::grade, header = "Grade")
    columnSheet(Produtos::unidade, header = "UN")
    columnSheet(Produtos::mesesGarantia, header = "Val")
    columnSheet(Produtos::qttyCompra, header = "Compra")
    columnSheet(Produtos::qttyVendas, header = "Qt Venda")
    columnSheet(Produtos::DS_TT, header = "DS")
    columnSheet(Produtos::MR_TT, header = "MR")
    columnSheet(Produtos::MF_TT, header = "MF")
    columnSheet(Produtos::PK_TT, header = "PK")
    columnSheet(Produtos::TM_TT, header = "TM")
    columnSheet(Produtos::forn, header = "Forn")
    columnSheet(Produtos::abrev, header = "Abrev")
    columnSheet(Produtos::tributacao, header = "Trib")
    columnSheet(Produtos::rotulo, header = "Rotulo")
    columnSheet(Produtos::tipo, header = "Tipo")
    columnSheet(Produtos::cl, header = "CL")
  }
}