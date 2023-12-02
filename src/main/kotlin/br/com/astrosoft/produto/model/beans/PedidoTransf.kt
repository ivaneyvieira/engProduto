package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class PedidoTransf(
  var lojaNoOri: Int,
  var lojaOrigem: String?,
  var ordno: String?,
  var lojaNoDes: Int?,
  var lojaDestino: String?,
  var rota: String?,
  var cliente: Int?,
  var clienteNome: String?,
  var userno: Int?,
  var usuario: String?,
  var data: LocalDate?,
  var vendedor: Int?,
  var localizacao: String?,
  var usuarioCD: String?,
  var totalProdutos: Double?,
  var marca: Int?,
  var cancelada: String?,
  var hora: LocalTime?,
  var situacao: Int?,
  var observacao: String?,
  var dataTransf: LocalDate?,
  var notaTransf: String?,
  var selfColor: String?,
  var referente: String?,
  var entregue: String?,
  var recebido: String?,
  var numSing: String?,
  var loginSing: String?,
  var nameSing: String?,
  var valorTransf: Double?,
  var observacaoTransf: String?,
  var userTransf: Int?,
  var nameTransf: String?,
  var userReserva: Int?,
  var nameReserva: String?,
) {

  private fun extrairNumeros(str: String): List<Int> {
    val regex = "\\d+".toRegex()
    return regex.findAll(str).mapNotNull { it.value.toIntOrNull() }.toList()
  }

  fun entregueRelatorio(): String {
    val listNumero = extrairNumeros(entregue ?: "")
    return if (listNumero.isEmpty()) entregue ?: ""
    else saci.findAllUser().firstOrNull { listNumero.contains(it.no) }?.name ?: entregue ?: ""
  }

  val situacaoPedido
    get() = when (situacao) {
      0    -> "Incluído"
      1    -> "Orçado"
      2    -> "Reservado"
      3    -> "Vendido"
      4    -> "Expirado"
      5    -> "Cancelado"
      6    -> "Reserva B"
      7    -> "Trânsito"
      8    -> "Futura"
      else -> "Outro"
    }

  val sing: String
    get() {
      return nameSing ?: ""
    }

  val situacaoCancelada
    get() = if (cancelada == "S") "Cancelada" else ""

  val observacaoLimpa: String
    get() {
      referente ?: entregue ?: recebido ?: return ""
      return "${referente ?: ""} | ${entregue ?: ""} | ${recebido ?: ""} | ${selfColor ?: ""}"
    }

  private fun splitCD(index: Int) = usuarioCD?.split("-")?.getOrNull(index) ?: ""

  val usuarioNameCD
    get() = splitCD(0)
  val dataCD
    get() = splitCD(1)
  val horaCD
    get() = splitCD(2)

  val chaveNovaCD: String
    get() = "$usuarioNameCD-$dataCD-$horaCD-$localizacao"

  fun produtos() = saci.findProdutoPedidoTransf(this)
  fun autoriza(user: UserSaci) {
    saci.autorizaPedidoTransf(this, user.no)
  }

  fun marca(imrpessora: Impressora) {
    saci.marcaPedidoImpresso(lojaNoOri, ordno?.toIntOrNull() ?: 0, imrpessora)
  }

  fun rotaPedido(): Rota {
    return Rota(lojaNoOri, lojaNoDes ?: 0)
  }

  fun mudaParaReservado(user: Int) {
    saci.mudaParaReservado(lojaNoOri, ordno?.toIntOrNull() ?: 0, user)
  }

  fun recebidoRelatorio(): CampoRelatorio {
    val recebidoStr = recebido?.trim() ?: ""
    return if (recebidoStr.startsWith("CLIENTE", ignoreCase = true)) {
      val splitReferente = referente?.split(" ") ?: emptyList()
      val nota = splitReferente.getOrNull(1)?.trim() ?: ""
      val splitNota = nota.split("/")
      val numero = splitNota.getOrNull(0)?.trim()?.toIntOrNull() ?: 0
      val serie = splitNota.getOrNull(1)?.trim() ?: ""
      val notaSaida =
          saci.findNota(numero, serie, data ?: LocalDate.now()) ?: return CampoRelatorio("Recebido", recebidoStr)
      return if (notaSaida.nomeCliente == null) CampoRelatorio("Recebido", recebidoStr)
      else CampoRelatorio("Recebido CLiente", notaSaida.nomeCliente ?: "")
    } else {
      CampoRelatorio("Recebido", recebidoStr)
    }
  }

  companion object {
    fun findTransf(filtro: FiltroPedidoTransf) = saci.findPedidoTransf(filtro)
  }
}

data class FiltroPedidoTransf(
  val storeno: Int,
  val pesquisa: String,
  val marca: EMarcaPedido,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val autorizado: Boolean?,
  val impresso: Boolean?,
)

data class Rota(val lojaOrigem: Int, val lojaDestino: Int)

data class CampoRelatorio(val label: String, val value: String)