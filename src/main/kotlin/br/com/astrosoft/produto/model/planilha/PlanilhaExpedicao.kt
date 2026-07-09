package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaSaida

class PlanilhaExpedicao : Planilha<NotaSaida>("Expedição") {
  init {
    columnSheet(NotaSaida::loja, header = "Loja")
    columnSheet(NotaSaida::usuarioSingExp, header = "Autoriza")
    columnSheet(NotaSaida::nota, header = "Nota")
    columnSheet(NotaSaida::data, header = "Data")
    columnSheet(NotaSaida::hora, header = "Hora")
    columnSheet(NotaSaida::rota, header = "Rota")
    columnSheet(NotaSaida::cliente, header = "Cliente")
    columnSheet(NotaSaida::nomeCliente, header = "Nome Cliente")
    columnSheet(NotaSaida::vendedor, header = "Vendedor")
    columnSheet(NotaSaida::valorNota, header = "Valor")
    columnSheet(NotaSaida::tipoNotaSaidaDesc, header = "Tipo")
    columnSheet(NotaSaida::cfop, header = "CFOP")
    columnSheet(NotaSaida::tipo, header = "Ent/Ret")
    columnSheet(NotaSaida::situacao, header = "Situação")
  }
}