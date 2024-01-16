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
  var usuarioDev: String?,
  var loginDev: String?,
  var observacao: String?,
) {
  fun delete() {
    saci.deleteNotaAutorizacao(this)
  }

  fun update() {
    saci.updateNotaAutorizacao(this)
  }

  fun autoriza(user: UserSaci) {
    this.usernoSing = user.no
    update()
  }

  companion object {
    fun findAll(filtro: FiltroNotaAutorizacao): List<NotaAutorizacao> {
      return saci.findNotaAutorizacao(filtro)
    }

    fun insert(chave: NotaAutorizacaoChave) {
      saci.insertNotaAutorizacao(chave)
    }

    fun findNota(loja: Int, nota: String): NotaAutorizacao? {
      val split = nota.trim().split("/")
      val nfno = split.firstOrNull()?.trim()?.toIntOrNull() ?: 0
      val nfse = split.lastOrNull()?.trim() ?: ""
      val lista = saci.findNotaAutorizacao(loja, nfno, nfse)
      return lista.firstOrNull {
        it.nfVenda == nota
      }
    }
  }
}

data class FiltroNotaAutorizacao(
  val loja: Int,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
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