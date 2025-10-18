package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class FornecedorLoja(
  var vendno: Int = 0,
  var abrev: String = "",
  var dataDS: LocalDate? = null,
  var usernoDS: Int = 0,
  var loginDS: String? = "",
  var dataMR: LocalDate? = null,
  var usernoMR: Int = 0,
  var loginMR: String? = "",
  var dataMF: LocalDate? = null,
  var usernoMF: Int = 0,
  var loginMF: String? = "",
  var dataPK: LocalDate? = null,
  var usernoPK: Int = 0,
  var loginPK: String? = "",
  var dataTM: LocalDate? = null,
  var usernoTM: Int = 0,
  var loginTM: String? = ""
) {
  fun updateData() {
    saci.saveFornLojaData(this)
  }

  fun updateUserno(loja: Int, userno: Int) {
    when (loja) {
      2 -> this.usernoDS = userno
      3 -> this.usernoMR = userno
      4 -> this.usernoMF = userno
      5 -> this.usernoPK = userno
      8 -> this.usernoTM = userno
    }
    saci.saveFornLojaUser(vendno = vendno, storeno = loja, userno = userno)
  }

  companion object {
    fun findAll(filtro: FiltroFornecedorLoja) = saci.findFornLoja(filtro)
  }
}

data class FiltroFornecedorLoja(val pesquisa: String, val dataInicial: LocalDate?, val dataFinal: LocalDate?)
