package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Validade(var validade: Int, var mesesFabricacao: Int) {
  fun salve() {
    saci.saveValidade(this)
  }

  fun delete() {
    saci.delValidade(this)
  }

  companion object {
    fun findAll(): List<Validade> {
      return saci.listValidade()
    }
  }
}