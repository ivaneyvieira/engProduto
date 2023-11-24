package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class EntradaDevCliProList(
  var dataI: LocalDate?,
  var dataF: LocalDate?,
  var codLoja: Int?,
  var loja: String?,
  var prdno: String?,
  var codigo: String?,
  var descricao: String?,
  var grade: String?,
  var quantidade: Int?
) {
  val codigoFormat
    get() = codigo?.padStart(6, '0') ?: ""

  companion object {
    fun findAll(filtro: FiltroEntradaDevCliProList) = saci.entradaDevCliProList(filtro)
  }
}

data class FiltroEntradaDevCliProList(
  val loja: Int,
  val dataI: LocalDate,
  val dataF: LocalDate
)