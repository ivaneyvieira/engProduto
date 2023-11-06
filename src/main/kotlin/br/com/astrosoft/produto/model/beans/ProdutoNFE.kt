package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoNFE(
  var loja: Int,
  var ni: Int,
  var chave: String,
  var nota: String,
  var codigo: String,
  var grade: String,
  var barcode: String,
  var referencia: String?,
  var mesesGarantia: Int?,
  var quantidadePacote: Int?,
  var descricao: String,
  var unidade: String,
  var vendno: Int,
  var fornecedor: String,
  var typeno: Int,
  var typeName: String,
  var clno: String,
  var clname: String,
  var precoCheio: Double,
  var ncm: String,
  var quantidade: Int,
  var preco: Double?,
  var total: Double?,
  var localizacao: String,
  var qttyRef: Int?,
  var marca: Int,
                ) {
  fun revomeProdutoReceber() {
    saci.removeProdutoReceber(this)
  }

  fun processaReceber() {
    marca = 1
    saci.updateProdutoReceber(this)
  }

  fun saveProdutoReceber() {
    saci.updateProdutoReceber(this)
  }
}