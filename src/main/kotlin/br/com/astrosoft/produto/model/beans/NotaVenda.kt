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
  var cliente: String?,
  var nomeCliente: String?,
  var vendedor: String?,
) {
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