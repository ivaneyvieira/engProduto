package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaResumoCartao(
  var loja: Int?,
  var pdv: Int?,
  var transacao: Int?,
  var mult: Double?,
  var data: LocalDate?,
  val dataFormatada: String? = null,
  var tipoPgto: String?,
  var documento: String?,
  var quantParcelas: Int?,
  var mediaPrazo: Double?,
  var valor: Double?,
  var valorFin: Double?,
  var valorTipo: Double?,
) {
  fun grupo(filtro: FiltroNotaResumoCartao): String {
    val grupoLoja = if (filtro.agrupaLojas) "" else loja.toString()
    val dataAgrupada = when (filtro.agrupaData) {
      AgrupaData.DIA -> data?.format("yyyy-MM-dd") ?: ""
      AgrupaData.MES -> data?.format("yyyy-MM") ?: ""
      AgrupaData.ANO -> data?.format("yyyy") ?: ""
    }

    val outroGrupo = "${mult.format("0.0000")}-$documento-$quantParcelas-${mediaPrazo.format()}-$tipoPgto"

    return "$grupoLoja-$dataAgrupada-$outroGrupo"
  }

  companion object {
    fun findAll(filtro: FiltroNotaResumoCartao): List<NotaResumoCartao> {
      return saci.findNotaResumoCartao(filtro).agrupa(filtro)
    }
  }
}

data class FiltroNotaResumoCartao(
  val loja: Int,
  val agrupaLojas: Boolean,
  val agrupaData: AgrupaData,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)

fun List<NotaResumoCartao>.agrupa(filtro: FiltroNotaResumoCartao): List<NotaResumoCartao> {
  val grupo = this.groupBy { it.grupo(filtro) }

  return grupo.values.mapNotNull { ent ->
    val first = ent.firstOrNull() ?: return@mapNotNull null
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

    NotaResumoCartao(
      loja = if (filtro.agrupaLojas) null else first.loja,
      pdv = null,
      transacao = null,
      mult = multMed,
      data = first.data,
      dataFormatada = dataAgrupada,
      tipoPgto = first.tipoPgto,
      documento = first.documento,
      quantParcelas = ent.maxOf { it.quantParcelas ?: 0 },
      mediaPrazo = mediaPrazo,
      valor = ent.sumOf { it.valor ?: 0.0 },
      valorTipo = ent.sumOf { it.valorTipo ?: 0.0 },
      valorFin = ent.sumOf { it.valorFin ?: 0.0 }
    )
  }
}