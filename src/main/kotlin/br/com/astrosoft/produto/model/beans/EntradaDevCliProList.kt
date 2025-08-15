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
  var userLogin: String?,
  var quantidade: Int?,
  var observacao: String?,
  var ni: Int?,
  var nota: String?,
  var valor: Double?,
  var tipo: String?,
  var tipoPrd: String?,
) {
  fun tipoPrdTratado(): String {
    val temProduto = this.tipoPrd?.endsWith(" P") == true
    return tipoNotaPre() + if (temProduto) " P" else ""
  }

  fun tipoNotaPre(): String {
    val tipoNota = this.tipo ?: return ""
    return when {
      "TRO.*".toRegex().matches(tipoNota) -> "TROCA"
      "EST.*".toRegex().matches(tipoNota) -> "ESTORNO"
      "REE.*".toRegex().matches(tipoNota) -> "REEMBOLSO"
      else                                -> tipoNota
    }
  }

  fun isTipoMisto(): Boolean {
    val tipoNota = this.tipo ?: ""
    return "TRO.* M.*".toRegex().matches(tipoNota) ||
           "EST.* M.*".toRegex().matches(tipoNota) ||
           "REE.* M.*".toRegex().matches(tipoNota)
  }

  val observacao01: String
    get() {
      val parte1 = observacao?.split(")")?.getOrNull(0) ?: return ""
      return "$parte1)"
    }

  val codigoFormat
    get() = codigo?.padStart(6, '0') ?: ""

  companion object {
    fun findAll(filtro: FiltroEntradaDevCliProList) = saci.entradaDevCliProList(filtro)
    fun findAll(listNi: List<Int>) = saci.entradaDevCliProList(listNi)
  }
}

data class FiltroEntradaDevCliProList(
  val loja: Int,
  val data: LocalDate,
  val pesquisa: String,
)