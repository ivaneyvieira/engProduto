package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.Config
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

  fun produtos(marca: EMarcaNota) = saci.findProdutoNF(this, marca, userLocais())

  companion object {
    fun find(filtro: FiltroNota) = saci.findNotaSaida(filtro, userLocais())
  }
}

fun userLocais() : List<String>{
  val username = Config.user as? UserSaci
  if(username?.admin == true) return listOf("TODOS")
  return username?.listLocais?.toList() ?: emptyList()
}

data class FiltroNota(val storeno: Int, val nota: String, val marca: EMarcaNota) {
  val nfno: Int
    get() = nota.split("/").getOrNull(0)?.toIntOrNull() ?: 0
  val nfse: String
    get() = nota.split("/").getOrNull(1) ?: ""
}

enum class EMarcaNota(val num: Int) {
  EXP(0), CD(1), TODOS(999)
}