package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class DadosNotaSaida {
  var storeno: Int? = null
  var pdvno: Int? = null
  var xano: String? = null
  var prdno: String? = null
  var grade: String? = null
  var ncm: String? = null
  var invnoObs: String? = null
  var qtde: Int? = null
  var valorUnitario: Double? = null
  var baseIcms: Double? = null
  var valorIcms: Double? = null
  var valorIpi: Double? = null
  var icmsAliq: Double? = null
  var ipiAliq: Double? = null
  var natureza: String? = null
  var pedido: Int? = null
  var dataPedido: LocalDate? = null
}