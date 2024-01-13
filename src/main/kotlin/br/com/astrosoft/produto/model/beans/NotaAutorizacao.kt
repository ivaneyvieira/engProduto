package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaAutorizacao(
  var loja: Int?,
  var pdv: Int?,
  var transacao: Int?,
  var nfVenda: String?,
  var nfno: Int?,
  var nfse: Int?,
  var dataEmissao: LocalDate?,
  var codCliente: Int?,
  var nomeCliente: String?,
  var valorVenda: Double?,
) {

  fun insert() {
    saci.insertNotaAutorizacao(this)
  }

  fun delete() {
    saci.deleteNotaAutorizacao(this)
  }

  companion object {
    fun findAll(filtro: FiltroNotaAutorizacao): List<NotaAutorizacao> {
      return saci.findNotaAutorizacao(filtro)
    }
  }
}

data class FiltroNotaAutorizacao(
  val loja: Int,
  val pesquisa: String,
  val dataInicial: LocalDate,
  val dataFinal: LocalDate,
)