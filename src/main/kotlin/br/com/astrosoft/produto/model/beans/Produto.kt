package br.com.astrosoft.produto.model.beans

class Produto(
  val custno: Int,
  val fornecedor: String,
  val vendno: Int,
  val fornecedorSap: Int,
  val email: String,
  val tipo: String,
  var obs: String,
             )