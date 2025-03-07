package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class MovManual(
  var loja: Int?,
  var transacao: Int?,
  var data: LocalDate?,
  var codigoProduto: Int?,
  var nomeProduto: String?,
  var grade: String?,
  var observacao: String?,
  var pedido: String?,
  var rotulo: String?,
  var tributacao: String?,
  var qtty: Int?,
  var estVarejo: Int?,
  var estAtacado: Int?,
  var estTotal: Int?,
  var tipo: String?,
) {
  fun estorno() {
    saci.estornoMovManual(this)
  }

  companion object {
    fun findMovManual(filter: MovManualFilter): List<MovManual> {
      return saci.findMovManual(filter)
    }
  }
}

data class MovManualFilter(
  val loja: Int,
  val query: String,
  val dataI: LocalDate?,
  val dataF: LocalDate?,
  val tipo: ETipoMovManul,
)

enum class ETipoMovManul(val codigo: String) {
  ENTRADA("E"), SAIDA("S"), TODOS("T")
}