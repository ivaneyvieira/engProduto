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

  val valorFormula: Double
    get() {
      val operador = formula?.getOrNull(0) ?: return 1.00
      if (operador != '*' && operador != '/') return 1.00
      val valor = formula?.substring(1)?.trim()?.split(" ")?.getOrNull(0)?.replace(',', '.') ?: return 1.00
      return if (valor.length > 1) {
        valor.substring(1).toDoubleOrNull() ?: 1.00
      } else {
        1.00
      }
    }

  val embalagemFator
    get() = valorFormula

  val fator: Double
    get() = when {
      formula?.firstOrNull() == '*' -> valorFormula
      valorFormula == 0.00 -> 1.00
      formula?.firstOrNull() == '/' -> (1.00 / valorFormula)
      else -> 1.00
    }

  fun save() {
    saci.listPedidoXmlSave(this)
  }
}