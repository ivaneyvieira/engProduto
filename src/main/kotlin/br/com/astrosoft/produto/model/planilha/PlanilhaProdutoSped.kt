package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoSped

class PlanilhaProdutoSped : Planilha<ProdutoSped>("Credito Cliente") {
  init {
    columnSheet(ProdutoSped::codigo, header = "Cód")
    columnSheet(ProdutoSped::descricao, header = "Descrição")
    columnSheet(ProdutoSped::unidade, header = "Un")
    columnSheet(ProdutoSped::rotulo, header = "Rotulo")
    columnSheet(ProdutoSped::tributacao, header = "Trib")
    columnSheet(ProdutoSped::forn, header = "Forn")
    columnSheet(ProdutoSped::abrev, header = "Abrev")
    columnSheet(ProdutoSped::ncm, header = "NCM")
    columnSheet(ProdutoSped::tipo, header = "Tipo")
    columnSheet(ProdutoSped::clno, header = "CL")
    columnSheet(ProdutoSped::refForn, header = "Ref Forn")
    columnSheet(ProdutoSped::saldo, header = "Saldo")
    columnSheet(ProdutoSped::configSt, header = "Conf St")
  }
}