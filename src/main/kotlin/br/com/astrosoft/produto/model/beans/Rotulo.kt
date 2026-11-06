package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Rotulo {
  var rotulo: String? = null
  var qt: Int? = null
  
  companion object{
    fun findAll(): List<Rotulo> = saci.rotulos()
  }
}