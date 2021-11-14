package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Loja(val no: Int, val sname: String, val name: String) {
  val descricao
    get() = "$no - $sname : $name"

  companion object {
    val lojaZero = Loja(0, "Todas", "")
    fun allLojas() = saci.allLojas()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Loja

    if (no != other.no) return false

    return true
  }

  override fun hashCode(): Int {
    return no
  }
}