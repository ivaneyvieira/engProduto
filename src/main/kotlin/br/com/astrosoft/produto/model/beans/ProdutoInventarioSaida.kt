package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoInventarioSaida(
  var loja: Int?,
  var prdno: String?,
  var grade: String?,
  var date: LocalDate?,
  var qtty: Int?,
){
  companion object{
    fun find(dataIncial: LocalDate): List<ProdutoInventarioSaida> {
      return saci.produtoValidadeSaida(dataIncial)
    }
  }
}

data class ChaveSaida(val loja: Int?, val prdno: String?, val grade: String?)