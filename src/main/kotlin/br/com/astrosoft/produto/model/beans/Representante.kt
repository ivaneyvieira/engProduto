package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Representante(
  var repno: Int?,
  var vendno: Int?,
  var nome: String,
  var telefone: String,
  var celular: String,
  var email: String,
  var contatos: List<RepresentanteContato>
) {
  fun saveEmail() {
    saci.saveEmail(this)
  }

  var emailList: Set<String>
    get() = email.split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()
    set(value) {
      email = value.joinToString(",")
    }
}

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