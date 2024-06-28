package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class DadosCliente {
  var custno: Int = 0
  var nome: String = ""
  var cpfCnpj: String = ""
  var tipoPessoa: String = ""
  var rg: String = ""
  var endereco: String = ""
  var bairro: String = ""
  var cidade: String = ""
  var estado: String = ""
  var ddd: String = ""
  var telefone: String = ""
  var celular: String = ""
  var rota: String? = ""

  val fone: String
    get() {
      val dddStr = if (ddd.isEmpty()) "" else "($ddd)"
      val numeros = listOf(telefone, celular).distinct().filter { it.isNotEmpty() }.joinToString("/")
      return "$dddStr $numeros"
    }

  companion object {
    fun findAll(filtro: FiltroDadosCliente): List<DadosCliente> {
      return saci.selectCliente(filtro)
    }
  }
}

data class FiltroDadosCliente(
  val pesquisa: String,
)