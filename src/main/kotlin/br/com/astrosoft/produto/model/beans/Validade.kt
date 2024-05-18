package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

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

    fun findValidade(validade: Int): Validade? {
      return findAll().firstOrNull { it.validade == validade }
    }
  }
}