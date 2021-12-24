package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Produto(
  val codigo: String,
  val grade: String,
  val barcode: String,
  val descricao: String,
  val vendno: Int,
  val fornecedor: String,
  val typeno: Int,
  val typeName: String,
  val clno: String,
  val clname: String,
  val altura: Int,
  val comprimento: Int,
  val largura: Int,
  val precoCheio: Double,
  val ncm: String,
             ) {

  companion object {
    fun find(filtro: FiltroProduto): List<Produto> {
      return saci.findProduto(filtro)
    }
  }
}
