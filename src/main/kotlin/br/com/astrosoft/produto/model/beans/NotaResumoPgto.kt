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
  var mediaPrazo: Double?,
  var valor: Double?,
  var cliente: Int?,
  var uf: String?,
  var nomeCliente: String?,
  var vendedor: String?,
  var valorFin: Double?,
  var valorTipo: Double?,
  var obs: String?,
  var contagem: Int? = 1,
  var perVenda: Double? = 0.00,
  val dataFormatada: String? = null
) {
  val numeroInterno: Int?
    get() {
      val regex = Regex("""NI[^0-9A-Z]*(\d+)""")
      val obsInput = obs?.uppercase() ?: return null
      val match = regex.find(obsInput) ?: return null
      val groups = match.groupValues
      return groups.getOrNull(1)?.toIntOrNull()
    }

  fun grupo(filtro: FiltroNotaResumoPgto): String {
    val agrupaLojas = filtro.agrupaLojas
    val agrupaParcelas = filtro.agrupaParcelas
    val agrupaTipoPagamento = filtro.agrupaTipoPagamento
    val agrupaDatas = filtro.agrupaDatas

    val dataAgrupada = when (agrupaDatas) {
      AgrupaData.DIA -> data?.format("yyyy-MM-dd") ?: ""
      AgrupaData.MES -> data?.format("yyyy-MM") ?: ""
      AgrupaData.ANO -> data?.format("yyyy") ?: ""
    }

    val grupoLoja = if (agrupaLojas) dataAgrupada
    else "$loja-$dataAgrupada"

    val grupoParcela = if (agrupaParcelas) {
      if (agrupaTipoPagamento)
        "${(mediaPrazo ?: 0.00).format()}-$tipoPgto"
      else
        (mediaPrazo ?: 0.00).format()
    } else {
      if (agrupaTipoPagamento)
        tipoPgto ?: ""
      else
        "${mult.format("0.0000")}-${(mediaPrazo ?: 0.00).format()}-$tipoPgto"
    }

    return "$grupoLoja-$grupoParcela"
  }

  companion object {
    fun findAll(filtro: FiltroNotaResumoPgto): List<NotaResumoPgto> {
      return saci.findNotaResumoPgto(filtro).filter {
        if (filtro.contaC) true
        else {
          val filtroValor = it.tipoPgto?.startsWith("Conta") ?: true
          filtroValor.not()
        }
      }.agrupa(filtro)
    }
  }
}

data class FiltroNotaResumoPgto(
  val loja: Int,
  val agrupaLojas: Boolean,
  val agrupaParcelas: Boolean,
  val agrupaTipoPagamento: Boolean,
  val agrupaDatas: AgrupaData,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val contaC: Boolean
)

fun List<NotaResumoPgto>.agrupa(filtro: FiltroNotaResumoPgto): List<NotaResumoPgto> {
  val grupo = this.groupBy { it.grupo(filtro) }
  val totalValor = this.sumOf { it.valorTipo ?: 0.0 }
  return grupo.values.mapNotNull { ent ->
    val first = ent.firstOrNull() ?: return@mapNotNull null
    val firstMetodo = ent.sortedByDescending { it.numMetodo ?: 0 }.firstOrNull { it.numMetodo != null }

    val dataAgrupada = when (filtro.agrupaDatas) {
      AgrupaData.DIA -> first.data?.format("dd/MM/yyyy") ?: ""
      AgrupaData.MES -> first.data?.format("MM/yyyy") ?: ""
      AgrupaData.ANO -> first.data?.format("yyyy") ?: ""
    }

    val agrupaTipoPagamento = filtro.agrupaTipoPagamento
    val mediaPrazo = if (agrupaTipoPagamento) {
      val totalTipo = ent.sumOf { it.valorTipo ?: 0.0 }
      val totalItens = ent.sumOf { (it.mediaPrazo ?: 0.00) * (it.valorTipo ?: 0.00) }
      totalItens / totalTipo
    } else {
      (first.mediaPrazo ?: 0.00) * 1.00
    }
    val multMed: Double? = if (agrupaTipoPagamento) {
      val totalTipo = ent.sumOf { it.valorTipo ?: 0.0 }
      val totalFin = ent.sumOf { it.valorFin ?: 0.0 }
      totalTipo / (totalTipo - totalFin)
    } else {
      first.mult ?: 0.00
    }

    NotaResumoPgto(
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
      valorFin = ent.sumOf { it.valorFin ?: 0.0 },
      valorTipo = ent.sumOf { it.valorTipo ?: 0.0 },
      obs = null,
      contagem = ent.sumOf { it.contagem ?: 0 },
      perVenda = ent.sumOf { it.valorTipo ?: 0.0 } * 100.00 / totalValor,
    )
  }
}

enum class AgrupaData(val descricao: String) {
  DIA("Dia"),
  MES("Mês"),
  ANO("Ano"),
}