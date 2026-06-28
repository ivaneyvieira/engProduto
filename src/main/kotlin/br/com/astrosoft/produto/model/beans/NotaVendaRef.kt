package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaVendaRef(
  var loja: Int?,
  var pdv: Int?,
  var transacao: Int?,
  var pedido: Int?,
  var numMetodo: Int?,
  var nomeMetodo: String?,
  var mult: Double?,
  var data: LocalDate?,
  var nota: String?,
  var tipoNf: String?,
  var hora: LocalTime?,
  var tipoPgto: String?,
  var documento: String?,
  var quantParcelas: Int?,
  var mediaPrazo: Int?,
  var valor: Double?,
  var cliente: Int?,
  var uf: String?,
  var nomeCliente: String?,
  var vendedor: String?,
  var valorTipo: Double?,
  var obs: String?,
) {
  var metodosPagamento: List<NotaVendaRef> = emptyList()

  val documentoStr: String
    get() {
      val doc = documento ?: return ""
      val quant = if (quantParcelas == null) "" else " (${quantParcelas}x)"
      return "$doc $quant"
    }

  fun produtos(): List<ProdutoNFS> {
    return saci.findProdutoNF(this)
  }

  val numeroInterno: Int?
    get() {
      val regex = Regex("""NI[^0-9A-Z]*(\d+)""")
      val obsInput = obs?.uppercase() ?: return null
      val match = regex.find(obsInput) ?: return null
      val groups = match.groupValues
      return groups.getOrNull(1)?.toIntOrNull()
    }

  companion object {
    fun findAll(filtro: FiltroNotaVendaRef): List<NotaVendaRef> {
      return saci.findNotaVendaRef(filtro)
    }
  }
}

fun List<NotaVendaRef>.agrupaDetalhe(): List<NotaVendaRef> {
  return this.groupBy { venda ->
    "${venda.loja} ${venda.pdv} ${venda.transacao}"
  }.mapNotNull { ent ->
    val item = ent.value.firstOrNull()

    if (ent.value.size > 1) {
      item?.metodosPagamento = ent.value
      item?.mediaPrazo = null
      item?.tipoPgto = null
      item?.valorTipo = ent.value.sumOf { it.valorTipo ?: 0.0 }
      item?.metodosPagamento = ent.value
    } else {
      item?.metodosPagamento = emptyList()
    }

    item
  }
}

data class FiltroNotaVendaRef(
  val loja: Int,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)