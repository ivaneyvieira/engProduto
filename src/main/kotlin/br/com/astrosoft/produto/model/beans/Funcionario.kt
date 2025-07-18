package br.com.astrosoft.produto.model.beans

class Funcionario {
  var codigo: Int = 0
  var nome: String = ""
  var login: String = ""
  var funcao: String = ""
  var senha: String = ""

  val nomeFuncao
    get() = "$nome ($funcao)"
}