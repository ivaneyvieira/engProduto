package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaSaida(val loja: Int,
                val pdvno: Int,
                val xano: Long,
                val numero: Int,
                val serie: String,
                val cliente: Int,
                val data: LocalDate,
                val vendedor: Int) {
  val nota
    get() = "$numero/$serie"

  fun produtos() = saci.findProdutoNF(this)

  companion object {
    fun find(filtro: FiltroNota) = saci.findNotaSaida(filtro)
  }
}

data class FiltroNota(val storeno: Int, val nota: String) {
  val nfno: Int
    get() = nota.split("/").getOrNull(0)?.toIntOrNull() ?: 0
  val nfse: String
    get() = nota.split("/").getOrNull(1) ?: ""
}