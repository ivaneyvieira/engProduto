package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class EntradaDevCliProList(
  var data: LocalDate?,
  var codLoja: Int?,
  var loja: String?,
  var prdno: String?,
  var codigo: String?,
  var descricao: String?,
  var grade: String?,
  var userName: String?,
  var quantidade: Int?,
  var observacao: String?,
  var ni: Int?,
  var nota: String?,
  var valor: Double?,
) {
  val observacao01: String
    get() {
      val parte1 = observacao?.split(")")?.getOrNull(0) ?: return ""
      return "$parte1)"
    }

  val observacao02: String
    get() {
      val parte2 = observacao?.split(")")?.getOrNull(1) ?: return ""
      return parte2.trim()
    }

  val codigoFormat
    get() = codigo?.padStart(6, '0') ?: ""

  companion object {
    fun findAll(filtro: FiltroEntradaDevCliProList) = saci.entradaDevCliProList(filtro)
  }
}

data class FiltroEntradaDevCliProList(
  val loja: Int,
  val data: LocalDate,
  val pesquisa: String,
)