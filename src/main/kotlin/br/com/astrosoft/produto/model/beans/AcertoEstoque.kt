package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class AcertoEstoque {
  var loja: Int = 0
  var pedido: Int = 0
  var data: LocalDate? = null
  var prdno: String = ""
  var grade: String = ""
  var quantidade: Int = 0
}