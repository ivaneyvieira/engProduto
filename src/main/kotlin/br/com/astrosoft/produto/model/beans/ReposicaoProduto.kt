package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class ReposicaoProduto(
  var loja: Int?,
  var numero: Int?,
  var data: LocalDate?,
  var localizacao: String?,
  var marca: Int?,
  var observacao: String?,
  var entregueNo: Int?,
  var entregueNome: String?,
  var entregueSNome: String?,
  var recebidoNo: Int?,
  var recebidoNome: String?,
  var recebidoSNome: String?,
  var prdno: String?,
  var codigo: String?,
  var barcode: String?,
  var descricao: String?,
  var quantidade: Int?,
  var qtRecebido: Int?,
)