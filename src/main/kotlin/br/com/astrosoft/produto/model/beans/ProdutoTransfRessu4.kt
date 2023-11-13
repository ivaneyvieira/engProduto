package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class ProdutoTransfRessu4(
  var storeno: Int,
  var pdvno: Int,
  var xano: Int,
  var notaTransf: String?,
  var rota: String?,
  var data: LocalDate?,
  var codigo: String?,
  var descricao: String?,
  var grade: String?,
  var codigoBarras: String?,
  var referencia: String?,
  var quant: Int?,
){
  val codigoFormat
    get() = codigo?.padStart(6, '0') ?: ""
}