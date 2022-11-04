package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.beans.UserSaci.Companion.userLocais
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class Ressuprimento(
  val numero: Long,
  val fornecedor: Int,
  val data: LocalDate?,
  val comprador: Int,
  val localizacao: String?,
  val usuarioCD: String?,
  val totalProdutos: Double,
  val marca: Int?,
  val cancelada: String?,
  val notaBaixa: String?,
  val dataBaixa: LocalDate?,
                   ) {

  val situacao
    get() = if (cancelada == "S") "Cancelada" else ""

  private fun splitCD(index: Int) = usuarioCD?.split("-")?.getOrNull(index) ?: ""

  val usuarioNameCD
    get() = splitCD(0)
  val dataCD
    get() = splitCD(1)
  val horaCD
    get() = splitCD(2)

  val chaveNovaCD: String
    get() = "$usuarioNameCD-$dataCD-$horaCD-$localizacao"

  fun produtos(marca: EMarcaRessuprimento) = saci.findProdutoRessuprimento(this, marca, userLocais())

  companion object {
    fun find(filtro: FiltroRessuprimento) = saci.findRessuprimento(filtro, userLocais())
  }
}

data class FiltroRessuprimento(val numero: Int, val marca: EMarcaRessuprimento)

enum class EMarcaRessuprimento(val num: Int, val descricao: String) {
  CD(0, "CD"), ENT(1, "Entregue"), TODOS(999, "Todos")
}