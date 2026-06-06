package br.com.astrosoft.produto.model.beans

data class AutorizaDevCliente(
  val invno: Int,
  val prdno: String,
  val grade: String,
  val userEntrega: Int,
  val userRecebimento: Int,
)
