package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
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
  var quantFat: Int? = 0
  var valorUnit: Double = 0.00
  var embalagem: Double = 0.00
  var formula: String? = null

  val valorFormula: Double?
    get() {
      val formula = formula?.replace(',', '.') ?: return null
      return if (formula.length > 1) {
        formula.substring(1).toDoubleOrNull() ?: 0.00
      } else {
        null
      }
    }

  val embalagemFator
    get() = valorFormula ?: embalagem

  val fator: Double?
    get() = if (formula?.firstOrNull() == '*') valorFormula
    else if (formula?.firstOrNull() == '/') 1 / (valorFormula ?: 1.0)
    else embalagem

  fun save() {
    saci.listPedidoXmlSave(this)
  }
}