package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaSaidaDev

class PlanilhaNFDAberta : Planilha<NotaSaidaDev>("NFD Aberta") {
  init {
    columnSheet(NotaSaidaDev::loja, "Loja")
    columnSheet(NotaSaidaDev::nota, "Nota")
    columnSheet(NotaSaidaDev::dataEmissao, "Data")
    columnSheet(NotaSaidaDev::cliente, "Cliente")
    columnSheet(NotaSaidaDev::nomeCliente, "Nome Cliente")
    columnSheet(NotaSaidaDev::valorNota, "Valor")
    columnSheet(NotaSaidaDev::observacaoNota, "Observação")
  }
}
