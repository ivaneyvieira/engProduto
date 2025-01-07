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

  val qtDS: Int?
    get() = if (qtPedido.toString().contains("2")) 2 else null

  val qtMR: Int?
    get() = if (qtPedido.toString().contains("3")) 3 else null

  val qtPK: Int?
    get() = if (qtPedido.toString().contains("5")) 5 else null

  val qtTM: Int?
    get() = if (qtPedido.toString().contains("8")) 8 else null

  fun pesquisaStr(): String {
    return "$prdno $codigo $descricao $barcode $grade $localizacao $vendno"
  }

  fun removerProduto() {
    saci.removeProdutoAcerto(this)
  }
}