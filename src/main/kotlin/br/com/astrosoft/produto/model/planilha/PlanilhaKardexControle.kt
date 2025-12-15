package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ControleKardec

class PlanilhaKardexControle : Planilha<ControleKardec>("ProdutoKardex") {
  init {
    columnSheet(ControleKardec::loja, "Loja")
    columnSheet(ControleKardec::data, "Data")
    columnSheet(ControleKardec::doc, "Doc")
    columnSheet(ControleKardec::tipoDescricao, "Tipo")
    columnSheet(ControleKardec::observacao, "Observação")
    columnSheet(ControleKardec::qtde, "Qtd")
    columnSheet(ControleKardec::saldo, "Saldo LJ")
  }
}