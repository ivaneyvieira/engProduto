package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Impressora(val no:Int, val name: String) {
  companion object {
    fun all() = saci.findImpressoras()
  }
}
