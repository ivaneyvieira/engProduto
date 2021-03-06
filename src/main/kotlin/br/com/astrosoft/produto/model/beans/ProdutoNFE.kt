package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoNFE(
  val loja: Int,
  val ni: Int,
  val chave: String,
  val nota: String,
  val codigo: String,
  val grade: String,
  val barcode: String,
  val referencia: String?,
  val mesesGarantia: Int?,
  val quantidadePacote: Int?,
  val descricao: String,
  val unidade: String,
  val vendno: Int,
  val fornecedor: String,
  val typeno: Int,
  val typeName: String,
  val clno: String,
  val clname: String,
  val precoCheio: Double,
  val ncm: String,
  var quantidade: Int,
  val preco: Double?,
  val total: Double?,
  val localizacao: String,
  val qttyRef: Int?,
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