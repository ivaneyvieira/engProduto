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
  var estoqueGeral: Int? = null
  var estoqueDS: Int? = null
  var estoqueMR: Int? = null
  var estoqueMF: Int? = null
  var estoquePK: Int? = null
  var estoqueTM: Int? = null
  var qtMov: Int? = null
  var mov: Int? = null

  val qtDS: Int?
    get() = if (qtPedido.toString().contains("2")) estoqueDS else null

  val qtMR: Int?
    get() = if (qtPedido.toString().contains("3")) estoqueMR else null

  val qtPK: Int?
    get() = if (qtPedido.toString().contains("5")) estoquePK else null

  val qtTM: Int?
    get() = if (qtPedido.toString().contains("8")) estoqueTM else null

  fun pesquisaStr(): String {
    return "$prdno $codigo $descricao $barcode $grade $localizacao $vendno"
  }

  fun removerProduto(lojaAcerto: Int) {
    saci.removeProdutoAcerto(this, lojaAcerto)
  }
}