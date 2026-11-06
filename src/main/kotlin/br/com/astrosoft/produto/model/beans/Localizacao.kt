package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Localizacao {
  var locAbrev: String? = null
  
  companion object {
    fun findByLocal(local: String): List<Localizacao>{
      return saci.localizacoes(local)
    }
  }
}