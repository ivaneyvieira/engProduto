package br.com.astrosoft.produto.model.beans

class Representante(
  var vendno: Int,
  var nome: String,
  var telefone: String,
  var celular: String,
  var email: String,
  var contatos: List<RepresentanteContato>
)

class RepresentanteContato(
  var vendno: Int,
  var repno: Int,
  var nome: String,
  var numPhone: Int,
  var telefone: String,
  var obsTel: String,
  var celular: String,
  var email: String
)