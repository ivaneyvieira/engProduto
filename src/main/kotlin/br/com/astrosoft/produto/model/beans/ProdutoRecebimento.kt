package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoRecebimento(
  var loja: Int?,
  var lojaAbrev: String?,
  var prdno: String?,
  var codigo: String?,
  var descricao: String?,
  var grade: String?,
  var unidade: String?,
  var validade: Int?,
  var vendno: Int?,
  var fornecedorAbrev: String?,
  var estoqueTotal: Int?,
  var date: LocalDate?,
  var mesAno: Int?,
  var qtty: Int?
){
  companion object{
    fun findEntradas(filtro: FiltroProdutoInventario, dataIncial: LocalDate?): List<ProdutoRecebimento> {
      return saci.produtoValidadeEntrada(filtro, dataIncial)
    }
  }
}