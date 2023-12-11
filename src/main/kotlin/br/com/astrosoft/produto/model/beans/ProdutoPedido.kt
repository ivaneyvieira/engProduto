package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format

data class ProdutoPedido(
  val codigo: String?,
  val descricao: String?,
  val grade: String?,
  val refFab: String?,
  val barcode: String?,
  var qtd: Int?,
  var peso: Double?,
  var vlUnit: Double?,
  var vlTotal: Double?,
  var localizacao: String?,
  var rotulo: String?,
) {
  var pedido: Pedido? = null

  val descricaoReport
    get() = "$descricao\n| ${peso.format()}   | $localizacao   | $refFab  | ${if (rotulo == "SUBSTIFC") " | SUBSTIFC" else ""}"
}