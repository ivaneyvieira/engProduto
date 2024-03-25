package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.EDirection
import br.com.astrosoft.framework.model.SqlLazy
import br.com.astrosoft.framework.model.SqlOrder
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaSaida(
  var loja: Int,
  var pdvno: Int,
  var xano: Long,
  var numero: Int,
  var serie: String?,
  var cliente: Int?,
  var nomeCliente: String?,
  var data: LocalDate?,
  var hora: LocalTime?,
  var vendedor: Int?,
  var nomeVendedor: String?,
  var locais: String?,
  var usuarioExp: String?,
  var usuarioCD: String?,
  var totalProdutos: Double?,
  var marca: Int?,
  var cancelada: String?,
  var tipoNotaSaida: String?,
  var notaEntrega: String?,
  var dataEntrega: LocalDate?,
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

  private fun splitCD(index: Int) = usuarioCD?.split("-")?.getOrNull(index) ?: ""

  val usuarioNameCD
    get() = splitCD(0)
  val dataCD
    get() = splitCD(1)
  val horaCD
    get() = splitCD(2)

  fun produtos(marca: EMarcaNota) = saci.findProdutoNF(this, marca)

  companion object {
    fun find(filtro: FiltroNota): List<NotaSaida> {

      return saci.findNotaSaida(
        filtro = filtro,
        SqlLazy(limit = 10000, orders = listOf(SqlOrder(property = "data", EDirection.DESC)))
      )
    }
  }
}

data class FiltroNota(
  val marca: EMarcaNota,
  val tipoNota: ETipoNota,
  val loja: Int,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val pesquisa: String,
)

enum class EMarcaNota(val num: Int, val descricao: String) {
  EXP(0, "Expedição"), CD(1, "CD"), ENT(2, "Entregue"), TODOS(999, "Todos")
}

enum class ETipoNota(val num: Int, val descricao: String) {
  NFCE(0, "NFCE"), NFE(1, "NFE"), TODOS(999, "Todos")
}