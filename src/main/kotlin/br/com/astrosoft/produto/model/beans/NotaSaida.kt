package br.com.astrosoft.produto.model.beans

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
  var valorNota: Double?,
  var data: LocalDate?,
  var hora: LocalTime?,
  var vendedor: Int?,
  var nomeVendedor: String?,
  var nomeCompletoVendedor: String?,
  var locais: String?,
  var usuarioExp: String?,
  var usuarioCD: String?,
  var totalProdutos: Double?,
  var marca: Int?,
  var cancelada: String?,
  var tipoNotaSaida: String?,
  var notaEntrega: String?,
  var usuarioEntrega: String?,
  var dataEntrega: LocalDate?,
  var tipo: String?,
  var countExp: Int?,
  var countCD: Int?,
  var countEnt: Int?,
  var countImp: Int?,
  var countNImp: Int?,
  var retiraFutura: Boolean?,
  var rota: String?,
  var enderecoCliente: String?,
  var bairroCliente: String?,
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

  val tipoNotaSaidaDesc: String
    get() {
      return ETipoNotaFiscal.entries.firstOrNull {
        it.name == tipoNotaSaida
      }?.descricao ?: ""
    }

  fun produtos(marca: EMarcaNota) = saci.findProdutoNF(this, marca)

  companion object {
    fun find(filtro: FiltroNota): List<NotaSaida> {
      val notas = saci.findNotaSaida(filtro = filtro) +
                  saci.findNotaSaida(
                    filtro = filtro.copy(
                      notaEntrega = "S",
                      tipoNota = filtro.tipoNota,
                    )
                  )
      return notas.distinctBy { "${it.loja} ${it.numero} ${it.serie} ${it.xano} ${it.pdvno}" }
    }
  }
}

data class FiltroNota(
  val marca: EMarcaNota,
  val marcaImpressao: Boolean?,
  val tipoNota: ETipoNotaFiscal,
  val loja: Int,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val notaEntrega: String = "N",
  val pesquisa: String,
)

enum class EMarcaNota(val num: Int, val descricao: String) {
  EXP(0, "Expedição"), CD(1, "CD"), ENT(2, "Entregue"), TODOS(999, "Todos")
}

enum class ETipoNotaFiscal(val descricao: String) {
  NFCE("NFCE"),
  NFE("NFE"),
  TRANSFERENCIA("Transferência"),
  ENTRE_FUT("Entrega Futura"),
  SIMP_REME("Retira Futura"),
  SIMP_REME_L("Retira Futura L"),
  ENTREGA_WEB("Entrega Web"),
  TODOS("Todos"),
}