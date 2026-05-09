package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.ControleKardex

class PlanilhaKardexControle : Planilha<ControleKardex>("ProdutoKardex") {
  init {
    columnSheet(ControleKardex::loja, "Loja")
    columnSheet(ControleKardex::data, "Data")
    columnSheet(ControleKardex::doc, "Doc")
    columnSheet(ControleKardex::tipoDescricao, "Tipo")
    columnSheet(ControleKardex::observacao, "Observação")
    columnSheet(ControleKardex::qtde, "Qtd")
    columnSheet(ControleKardex::saldo, "Saldo LJ")
  }
}