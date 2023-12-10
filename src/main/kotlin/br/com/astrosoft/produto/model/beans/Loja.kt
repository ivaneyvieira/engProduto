package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Loja(var no: Int, var sname: String, var name: String) {
  val descricao
    get() = "$no - $sname"

  companion object {
    val lojaZero = Loja(0, "Todas", "")
    fun allLojas() = saci.allLojas()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Loja

    return no == other.no
  }

  override fun hashCode(): Int {
    return no
  }
}