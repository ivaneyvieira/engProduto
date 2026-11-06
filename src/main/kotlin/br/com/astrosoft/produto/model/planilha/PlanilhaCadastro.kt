package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ProdutoCadastro

class PlanilhaCadastro : Planilha<ProdutoCadastro>("Produto") {
  init {
    columnSheet(ProdutoCadastro::codigo, header = "Cód")
    columnSheet(ProdutoCadastro::descricao, header = "Descrição")
    columnSheet(ProdutoCadastro::unidade, header = "Un")
    columnSheet(ProdutoCadastro::convEnt, header = "Conv Ent")
    columnSheet(ProdutoCadastro::rotulo, header = "Rotulo")
    columnSheet(ProdutoCadastro::tributacao, header = "Trib")
    columnSheet(ProdutoCadastro::forn, header = "Forn")
    columnSheet(ProdutoCadastro::abrev, header = "Abrev")
    columnSheet(ProdutoCadastro::ncm, header = "NCM")
    columnSheet(ProdutoCadastro::tipo, header = "Tipo")
    columnSheet(ProdutoCadastro::clno, header = "CL")
    columnSheet(ProdutoCadastro::refForn, header = "Ref Forn")
    columnSheet(ProdutoCadastro::pesoBruto, header = "P.Bruto")
    columnSheet(ProdutoCadastro::uGar, header = "U.Gar")
    columnSheet(ProdutoCadastro::tGar, header = "T.Gar")
    columnSheet(ProdutoCadastro::emb, header = "Emb")
    columnSheet(ProdutoCadastro::foraLinha, header = "F.Linha")
    columnSheet(ProdutoCadastro::saldo, header = "Saldo")
    columnSheet(ProdutoCadastro::configSt, header = "Conf St")
  }
}