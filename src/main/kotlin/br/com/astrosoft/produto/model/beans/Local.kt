package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Local(val abreviacao: String) {
  companion object {
    fun all() = saci.findLocais()
  }
}