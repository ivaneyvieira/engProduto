package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class PedidoXML {
  var loja: Int = 0
  var pedido: Int = 0
  var data: LocalDate? = null
  var prdno: String = ""
  var grade: String = ""
  var codigo: Int = 0
  var descricao: String = ""
  var refFor: String = ""
  var barcode: String? = ""
  var unidade: String = ""
  var quant: Int = 0
  var valorUnit: Double = 0.00
  var embalagem: Double = 0.00
}