package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class PedidoProduto {
  var loja: Int = 0
  var pedido: Int = 0
  var data: LocalDate? = null
  var status: Int = 0
  var no: Int = 0
  var fornecedor: String = ""
  var invno: Int? = null
  var dataEmissao: LocalDate? = null
  var dataEntrada: LocalDate? = null
  var nfEntrada: String? = null
  var total: Double = 0.0
  var codigo: String = ""
  var prdno: String = ""
  var descricao: String = ""
  var grade: String = ""
  var qtty: Int = 0
  var qttyCancel: Int = 0
  var qttyRcv: Int = 0
  var qttyPendente: Int = 0
  var custo: Double = 0.0
  var totalProduto: Double = 0.0
  var totalProdutoPendente: Double = 0.0
}