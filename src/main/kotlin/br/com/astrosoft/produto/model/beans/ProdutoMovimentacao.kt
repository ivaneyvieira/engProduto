package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoMovimentacao(
  var lojaOrigem: Int?,
  var lojaDestino: Int?,
  var abrevDestino: String?,
  var prdno: String?,
  var grade: String?,
  var date: LocalDate?,
  var qtty: Int?,
){
  companion object{
    fun findSaidas(dataIncial: LocalDate): List<ProdutoMovimentacao> {
      return saci.produtoValidadeSaida(dataIncial)
    }

    fun findEntradas(dataIncial: LocalDate): List<ProdutoMovimentacao> {
      return saci.produtoValidadeEntrada(dataIncial)
    }
  }
}

data class ChaveMovimentacao(val lojaOrigem: Int?, val prdno: String?, val grade: String?)