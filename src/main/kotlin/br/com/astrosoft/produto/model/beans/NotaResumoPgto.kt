package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaResumoPgto(
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
  val documentoStr: String
    get() {
      val doc = documento ?: return ""
      val quant = if (quantParcelas == null) "" else " (${quantParcelas}x)"
      return "$doc $quant"
    }

  val numeroInterno: Int?
    get() {
      val regex = Regex("""NI[^0-9A-Z]*(\d+)""")
      val obsInput = obs?.uppercase() ?: return null
      val match = regex.find(obsInput) ?: return null
      val groups = match.groupValues
      return groups.getOrNull(1)?.toIntOrNull()
    }

  fun grupo(): String {
    return "$loja-$data-$numMetodo-${mult.format("0.0000")}-${quantParcelas.format()}-${mediaPrazo.format()}-$tipoPgto"
  }

  companion object {
    fun findAll(filtro: FiltroNotaResumoPgto): List<NotaResumoPgto> {
      return saci.findNotaResumoPgto(filtro).agrupa()
    }
  }
}

data class FiltroNotaResumoPgto(
  val loja: Int,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)

fun List<NotaResumoPgto>.agrupa(): List<NotaResumoPgto> {
  val grupo = this.groupBy { it.grupo() }
  return grupo.values.mapNotNull { ent ->
    val first = ent.firstOrNull() ?: return@mapNotNull null
    NotaResumoPgto(
      loja = first.loja,
      pdv = null,
      transacao = null,
      pedido = null,
      numMetodo = first.numMetodo,
      nomeMetodo = first.nomeMetodo,
      mult = first.mult,
      data = first.data,
      nota = null,
      tipoNf = null,
      hora = null,
      tipoPgto = first.tipoPgto,
      documento = first.documento,
      quantParcelas = first.quantParcelas,
      mediaPrazo = first.mediaPrazo,
      valor = ent.sumOf { it.valor ?: 0.0 },
      cliente = null,
      uf = null,
      nomeCliente = null,
      vendedor = null,
      valorTipo = ent.sumOf { it.valorTipo ?: 0.0 },
      obs = null,
    )
  }
}