package br.com.astrosoft.produto.model.beans

class Funcionario(var codigo: Int?, var nome: String?, var funcao: String?){
  val nomeFuncao
    get() = "$nome ($funcao)"
}