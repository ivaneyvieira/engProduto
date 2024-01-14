package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaAutorizacao(
  var loja: Int?,
  var pdv: Int?,
  var transacao: Int?,
  var nfVenda: String?,
  var dataEmissao: LocalDate?,
  var codCliente: Int?,
  var nomeCliente: String?,
  var valorVenda: Double?,
  var tipoDev: String?,
  var usernoSing: Int?,
  var autorizacao: String?,
  var ni: Int?,
  var nfDev: String?,
  var dataDev: LocalDate?,
  var valorDev: Double?,
) {
  fun delete() {
    saci.deleteNotaAutorizacao(this)
  }

  fun update() {
    saci.updateNotaAutorizacao(this)
  }

  companion object {
    fun findAll(filtro: FiltroNotaAutorizacao): List<NotaAutorizacao> {
      return saci.findNotaAutorizacao(filtro)
    }

    fun insert(chave: NotaAutorizacaoChave) {
      saci.insertNotaAutorizacao(chave)
    }
  }
}

data class FiltroNotaAutorizacao(
  val loja: Int,
  val pesquisa: String,
  val dataInicial: LocalDate,
  val dataFinal: LocalDate,
)

data class NotaAutorizacaoChave(
  val loja: Int,
  val notaFiscal: String,
) {
  val nfno
    get() = notaFiscal.substringBefore("/").toIntOrNull() ?: 0

  val nfse
    get() = notaFiscal.substringAfter("/").toIntOrNull() ?: 0
}