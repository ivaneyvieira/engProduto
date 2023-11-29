package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Impressora(var no: Int, var name: String) {
  companion object {
    fun all() = saci.findImpressoras()
    fun allTermica() = all().filter { it.name.contains("Termica", ignoreCase = true) }
    fun findImpressora(loja: Int?): Impressora? {
      loja ?: return null
      val impressoras = allTermica()
      return impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
    }

    val TODAS = Impressora(0, "Todas")
    val ROTA = Impressora(9999, "Rota")
  }
}
