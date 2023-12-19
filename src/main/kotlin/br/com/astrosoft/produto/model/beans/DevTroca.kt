package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class DevTroca(
  var loja: Int,
  var pdv: Int,
  var xano: Int,
  var data: LocalDate?,
  var nfVenda: String?,
  var vlTotal: Double?,
  var ni: Int?,
  var cliente: Int?,
) {
  companion object {
    fun findAll(filtro: FiltroDevTroca): List<DevTroca> {
      return saci.findDevTroca(filtro)
    }
  }
}

data class FiltroDevTroca(
  val loja: Int,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)