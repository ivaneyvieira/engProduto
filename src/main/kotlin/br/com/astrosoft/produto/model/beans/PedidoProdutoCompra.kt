package br.com.astrosoft.produto.model.beans

class PedidoProdutoCompra {
  var loja: Int = 0
  var pedido: Int = 0
  var codigo: String = ""
  var barcode: String = ""
  var refFor: String = ""
  var descricao: String = ""
  var un: String = ""
  var grade: String = ""
  var qttyPedido: Int = 0
  var qttyPendente: Int = 0
  var custo: Double = 0.0

  val valorTotal: Double
    get() = qttyPedido * custo

  //Sequencial do item
  var item: Int = 0
}