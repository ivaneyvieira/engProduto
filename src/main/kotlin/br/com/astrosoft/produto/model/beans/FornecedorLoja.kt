package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class FornecedorLoja(
  var vendno: Int = 0,
  var abrev: String = "",
  var dataDS: LocalDate? = null,
  var dataMR: LocalDate? = null,
  var dataMF: LocalDate? = null,
  var dataPK: LocalDate? = null,
  var dataTM: LocalDate? = null
) {
  fun update() {
    saci.saveFornLoja(this)
  }

  companion object {
    fun findAll(filtro :FiltroFornecedorLoja) = saci.findFornLoja(filtro)
  }
}

data class FiltroFornecedorLoja(val pesquisa: String, val data: LocalDate?)
