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
                val vendedor: Int,
                val localizacao: String?,
                val usuarioExp: String?,
                val usuarioCD: String?,
                val totalProdutos: Double,
                val marca: Int?) {
  val nota
    get() = "$numero/$serie"

  val chaveExp: String?
    get() {
      val split = usuarioExp?.split("_") ?: return null
      val usuario = split.getOrNull(0) ?: ""
      val data = split.getOrNull(1) ?: ""
      val hora = split.getOrNull(2) ?: ""
      return usuario + "_" + nota + "_" + data + "_" + hora + "_" + localizacao
    }

  val chaveCD: String?
    get() {
      val split = usuarioCD?.split("_") ?: return null
      val usuario = split.getOrNull(0) ?: ""
      val data = split.getOrNull(1) ?: ""
      val hora = split.getOrNull(2) ?: ""
      return "Entregue" + "_" + usuario + "_" + nota + "_" + data + "_" + hora + "_" + localizacao
    }

  fun produtos(marca: EMarcaNota) = saci.findProdutoNF(this, marca, userLocais())

  companion object {
    fun find(filtro: FiltroNota) = saci.findNotaSaida(filtro, userLocais())
  }
}

fun userLocais(): List<String> {
  val username = Config.user as? UserSaci
  if (username?.admin == true) return listOf("TODOS")
  return username?.listLocais?.toList() ?: emptyList()
}

data class FiltroNota(val storeno: Int, val nota: String, val marca: EMarcaNota) {
  val nfno: Int
    get() = nota.split("/").getOrNull(0)?.toIntOrNull() ?: 0
  val nfse: String
    get() = nota.split("/").getOrNull(1) ?: ""
}

enum class EMarcaNota(val num: Int, val descricao: String) {
  EXP(0, "Expedição"), CD(1, "CD"), ENT(2, "Entregue"), TODOS(999, "Todos")
}