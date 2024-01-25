package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaVenda(
  var loja: Int?,
  var pdv: Int?,
  var transacao: Int?,
  var pedido: Int?,
  var data: LocalDate?,
  var nota: String?,
  var tipoNf: String?,
  var hora: LocalTime?,
  var tipoPgto: String?,
  var valor: Double?,
  var cliente: Int?,
  var nomeCliente: String?,
  var vendedor: String?,
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

  companion object {
    fun findAll(filtro: FiltroNotaVenda): List<NotaVenda> {
      return saci.findNotaVenda(filtro)
    }
  }
}

data class FiltroNotaVenda(
  val loja: Int,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)