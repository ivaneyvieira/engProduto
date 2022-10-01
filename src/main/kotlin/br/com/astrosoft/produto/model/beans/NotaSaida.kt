package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.model.EDirection
import br.com.astrosoft.framework.model.SqlLazy
import br.com.astrosoft.framework.model.SqlOrder
import br.com.astrosoft.produto.model.beans.UserSaci.Companion.userLocais
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaSaida(
  val loja: Int,
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
  val marca: Int?,
  val cancelada: String?,
  val tipoNotaSaida: String?,
  val notaEntrega: String?,
  val dataEntrega: LocalDate?,
               ) {
  val nota
    get() = "$numero/$serie"

  val situacao
    get() = if (cancelada == "S") "Cancelada" else ""

  private fun splitExp(index: Int) = usuarioExp?.split("-")?.getOrNull(index) ?: ""

  val usuarioNameExp
    get() = splitExp(0)
  val dataExp
    get() = splitExp(1)
  val horaExp
    get() = splitExp(2)

  val chaveNovaExp: String
    get() = "$usuarioNameExp-$dataExp-$horaExp-$localizacao"

  private fun splitCD(index: Int) = usuarioCD?.split("-")?.getOrNull(index) ?: ""

  val usuarioNameCD
    get() = splitCD(0)
  val dataCD
    get() = splitCD(1)
  val horaCD
    get() = splitCD(2)

  val chaveNovaCD: String
    get() = "$usuarioNameCD-$dataCD-$horaCD-$localizacao"

  fun produtos(marca: EMarcaNota) = saci.findProdutoNF(this, marca, userLocais())

  companion object {
    fun find(filtro: FiltroNota): List<NotaSaida> {
      val user = Config.user as? UserSaci
      return saci.findNotaSaida(filtro = filtro,
                                locais = userLocais(),
                                user = user,
                                SqlLazy(limit = 10000, orders = listOf(SqlOrder(property = "data", EDirection.DESC))))
    }
  }
}

data class FiltroNota(
  val storeno: Int,
  val nota: String,
  val marca: EMarcaNota,
  val loja: Int,
  val cliente: Int,
  val vendedor: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
                     ) {
  val nfno: Int
    get() = nota.split("/").getOrNull(0)?.toIntOrNull() ?: 0
  val nfse: String
    get() = nota.split("/").getOrNull(1) ?: ""
}

enum class EMarcaNota(val num: Int, val descricao: String) {
  EXP(0, "Expedição"), CD(1, "CD"), ENT(2, "Entregue"), TODOS(999, "Todos")
}