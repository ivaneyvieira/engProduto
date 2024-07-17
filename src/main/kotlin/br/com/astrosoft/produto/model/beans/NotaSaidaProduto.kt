package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

data class NotaSaidaProduto(
  val motorista: String,
  val dataEntrega: LocalDate?,
  var usernoPrint: Int?,
  var usuarioPrint: String?,
  val loja: Int,
  val pedido: String,
  val nota: String,
  val data: LocalDate?,
  val cliente: String,
  val valorNota: Double,
  val codigo: String,
  val descricao: String,
  val grade: String,
  val quantidade: Int,
)