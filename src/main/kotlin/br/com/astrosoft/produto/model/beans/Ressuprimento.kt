package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.beans.UserSaci.Companion.userLocais
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class Ressuprimento(val loja: Int,
                    val ordno: Int,
                    val cliente: Int,
                    val data: LocalDate,
                    val vendedor: Int,
                    val localizacao: String?,
                    val usuarioExp: String?,
                    val usuarioCD: String?,
                    val totalProdutos: Double,
                    val marca: Int?,
                    val cancelada: String?) {

  val situacao
    get() = if (cancelada == "S") "Cancelada" else ""

  val chaveExp: String?
    get() {
      val split = usuarioExp?.split("-") ?: return null
      val usuario = split.getOrNull(0) ?: ""
      val data = split.getOrNull(1) ?: ""
      val hora = split.getOrNull(2) ?: ""
      return usuario + "-" + ordno + "-" + data + "-" + hora + "-" + localizacao
    }

  val chaveCD: String?
    get() {
      val split = usuarioCD?.split("-") ?: return null
      val usuario = split.getOrNull(0) ?: ""
      val data = split.getOrNull(1) ?: ""
      val hora = split.getOrNull(2) ?: ""
      return "Entregue" + "-" + usuario + "-" + ordno + "-" + data + "-" + hora + "-" + localizacao
    }

  fun produtos(marca: EMarcaRessuprimento) = saci.findProdutoRessuprimento(this, marca, userLocais())

  companion object {
    fun find(filtro: FiltroRessuprimento) = saci.findRessuprimento(filtro, userLocais())
  }
}

data class FiltroRessuprimento(val storeno: Int, val pedido: Int, val marca: EMarcaRessuprimento)

enum class EMarcaRessuprimento(val num: Int, val descricao: String) {
  CD(0, "CD"), ENT(1, "Entregue"), TODOS(999, "Todos")
}