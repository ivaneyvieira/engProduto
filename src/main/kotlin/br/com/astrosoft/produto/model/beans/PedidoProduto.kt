package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class PedidoProduto {
  var loja: Int = 0
  var pedido: Int = 0
  var data: LocalDate? = null
  var no: Int = 0
  var fornecedor: String = ""
  var total: Double = 0.0
  var codigo: String = ""
  var prdno: String = ""
  var descricao: String = ""
  var qtty: Int = 0
  var custo: Double = 0.0
  var totalProduto: Double = 0.0
}