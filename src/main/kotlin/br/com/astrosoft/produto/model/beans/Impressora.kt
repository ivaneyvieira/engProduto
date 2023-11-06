package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Impressora(var no: Int, var name: String) {
  companion object {
    fun all() = saci.findImpressoras()
  }
}
