package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoAcerto {
  var loja: Int? = null
  var pedido: Int? = null
  var prdno: String? = null
  var codigo: String? = null
  var barcode: String? = null
  var descricao: String? = null
  var grade: String? = null
  var vendno: Int? = null
  var localizacao: String? = null
  var validade: Int? = null
  var qtPedido: Int? = null
  var estoque: Int? = null
  var qtMov: Int? = null
  var mov: Int? = null

  fun pesquisaStr(): String {
    return "$prdno $codigo $descricao $barcode $grade $localizacao $vendno"
  }

  fun removerProduto() {
    saci.removeProdutoAcerto(this)
  }
}