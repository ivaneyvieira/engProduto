package br.com.astrosoft.produto.model.beans

class ProdutoNotaEntrada {
  var tipo: String? = null
  var loja: Int? = null
  var prdno: String? = null
  var descricao: String? = null
  var grade: String? = null
  var barcode: String? = null
  var movimentacao: Int? = null
  var paymno: Int? = null
  var lojaCliente: Int? = null

  val codigo
    get() = this.prdno?.trim()?.toIntOrNull() ?: 0
}