package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Local {
  var abreviacao: String? = null

  companion object {
    fun all() = saci.findLocais()
  }
}