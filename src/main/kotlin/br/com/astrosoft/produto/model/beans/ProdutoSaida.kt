package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoSaida(
  var lojaOrigem: Int?,
  var abrevLoja: String?,
  var lojaDestino: Int?,
  var abrevDestino: String?,
  var prdno: String?,
  var codigo: String?,
  var descricao: String?,
  var unidade: String?,
  var validade: Int?,
  var vendno: Int?,
  var fornecedorAbrev: String?,
  var estoqueTotal: Int?,
  var grade: String?,
  var date: LocalDate?,
  var qtty: Int?,
){
  companion object{
    fun findSaidas(filtro: FiltroProdutoInventario, dataIncial: LocalDate?): List<ProdutoSaida> {
      return saci.produtoValidadeSaida(filtro, dataIncial)
    }
  }
}

data class ChaveMovimentacao(val lojaOrigem: Int?, val prdno: String?, val grade: String?)