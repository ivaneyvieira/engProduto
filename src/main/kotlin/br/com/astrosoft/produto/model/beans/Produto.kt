package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Produto(
  var codigo: String,
  var grade: String,
  var barcode: String,
  var descricao: String,
  var vendno: Int,
  var fornecedor: String,
  var typeno: Int,
  var typeName: String,
  var clno: String,
  var clname: String,
  var altura: Int,
  var comprimento: Int,
  var largura: Int,
  var precoCheio: Double,
  var ncm: String,
  var localizacao: String,
) {

  companion object {
    fun find(filtro: FiltroProduto): List<Produto> {
      return saci.findProduto(filtro)
    }
  }
}
