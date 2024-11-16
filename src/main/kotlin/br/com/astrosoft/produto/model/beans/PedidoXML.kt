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
  var formula: String? = null

  val valorFormula: Double
    get() {
      val formula = formula?.replace(',', '.') ?: return 1.00
      return if (formula.length > 1) {
        formula.substring(1).toDoubleOrNull() ?: 0.00
      } else {
        1.00
      }
    }

  val fator: Double
    get() = if (formula?.firstOrNull() == '*') valorFormula
    else if (formula?.firstOrNull() == '/') 1 / valorFormula
    else embalagem
}