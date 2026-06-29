package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class ParcelasVenda {
  var loja: Int? = null
  var pdv: Int? = null
  var transacao: Int? = null
  var dataVenda: LocalDate? = null
  var seqno: Int? = null
  var tipo: String? = null
  var dataParcela: LocalDate? = null
  var valorParcela: Double? = null
  var documento: String? = null
}