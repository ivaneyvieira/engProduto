package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class AcertoEntrada(
  var loja: Int,
  var ni: Int,
  var notaFiscal: String?,
  var dataEmissao: LocalDate?,
  var fornecedor: Int?,
  var nomeFornecedo: String?,
  var observacao: String?,
  var codigoProduto: String?,
  var nomeProduto: String?,
  var grade: String?,
  var quantidade: Int?,
  var valorUnitario: Double?,
  var valorTotal: Double?,
)