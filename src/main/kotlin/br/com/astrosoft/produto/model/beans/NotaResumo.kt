package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaResumo(
  var loja: Int?,
  var pdv: Int?,
  var transacao: Int?,
  var pedido: Int?,
  var numMetodo: Int?,
  var nomeMetodo: String?,
  var mult: Double?,
  var data: LocalDate?,
  val dataFormatada: String? = null,
  var nota: String?,
  var tipoNf: String?,
  var hora: LocalTime?,
  var tipoPgto: String?,
  var documento: String?,
  var quantParcelas: Int?,
  var mediaPrazo: Double?,
  var valor: Double?,
  var cliente: Int?,
  var uf: String?,
  var nomeCliente: String?,
  var vendedor: String?,
  var valorFin: Double?,
  var valorTipo: Double?,
  var obs: String?,
) {
  val numeroInterno: Int?
    get() {
      val regex = Regex("""NI[^0-9A-Z]*(\d+)""")
      val obsInput = obs?.uppercase() ?: return null
      val match = regex.find(obsInput) ?: return null
      val groups = match.groupValues
      return groups.getOrNull(1)?.toIntOrNull()
    }

  fun grupo(filtro: FiltroNotaResumo): String {
    val grupoLoja = if (filtro.agrupaLojas) "" else loja.toString()
    val dataAgrupada = when (filtro.agrupaData) {
      AgrupaData.DIA -> data?.format("yyyy-MM-dd") ?: ""
      AgrupaData.MES -> data?.format("yyyy-MM") ?: ""
      AgrupaData.ANO -> data?.format("yyyy") ?: ""
    }

    val outroGrupo = "$numMetodo-${mult.format("0.0000")}-$documento-$quantParcelas-${mediaPrazo.format()}-$tipoPgto"

    return "$grupoLoja-$dataAgrupada-$outroGrupo"
  }

  companion object {
    fun findAll(filtro: FiltroNotaResumo): List<NotaResumo> {
      return saci.findNotaResumo(filtro).agrupa(filtro)
    }
  }
}

data class FiltroNotaResumo(
  val loja: Int,
  val agrupaLojas: Boolean,
  val agrupaData: AgrupaData,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)

fun List<NotaResumo>.agrupa(filtro: FiltroNotaResumo): List<NotaResumo> {
  val grupo = this.groupBy { it.grupo(filtro) }
  val totalValor = this.sumOf { it.valorTipo ?: 0.0 }
  return grupo.values.mapNotNull { ent ->
    val first = ent.firstOrNull() ?: return@mapNotNull null
    val firstMetodo = ent.sortedByDescending { it.numMetodo ?: 0 }.firstOrNull { it.numMetodo != null }

    val dataAgrupada = when (filtro.agrupaData) {
      AgrupaData.DIA -> first.data?.format("dd/MM/yyyy") ?: ""
      AgrupaData.MES -> first.data?.format("MM/yyyy") ?: ""
      AgrupaData.ANO -> first.data?.format("yyyy") ?: ""
    }

    val mediaPrazo = run {
      val totalTipo = ent.sumOf { it.valorTipo ?: 0.0 }
      val totalItens = ent.sumOf { (it.mediaPrazo ?: 0.00) * (it.valorTipo ?: 0.00) }
      totalItens / totalTipo
    }
    val multMed: Double = run {
      val totalTipo = ent.sumOf { it.valorTipo ?: 0.0 }
      val totalFin = ent.sumOf { it.valorFin ?: 0.0 }
      totalTipo / (totalTipo - totalFin)
    }

    NotaResumo(
      loja = if (filtro.agrupaLojas) null else first.loja,
      pdv = null,
      transacao = null,
      pedido = null,
      numMetodo = firstMetodo?.numMetodo,
      nomeMetodo = firstMetodo?.nomeMetodo,
      mult = multMed,
      data = first.data,
      dataFormatada = dataAgrupada,
      nota = null,
      tipoNf = null,
      hora = null,
      tipoPgto = first.tipoPgto,
      documento = first.documento,
      quantParcelas = ent.maxOf { it.quantParcelas ?: 0 },
      mediaPrazo = mediaPrazo,
      valor = ent.sumOf { it.valor ?: 0.0 },
      cliente = null,
      uf = null,
      nomeCliente = null,
      vendedor = null,
      valorTipo = ent.sumOf { it.valorTipo ?: 0.0 },
      valorFin = ent.sumOf { it.valorFin ?: 0.0 },
      obs = null,
    )
  }
}