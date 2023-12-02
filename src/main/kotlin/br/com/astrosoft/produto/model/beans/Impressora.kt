package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Impressora(var no: Int, var name: String) {
  companion object {
    fun all() = saci.findImpressoras()
    fun allTermica() = all().filter { it.name.contains("Termica", ignoreCase = true) }
    fun findImpressora(loja: Int?, tipoRota: ETipoRota): Impressora? {
      loja ?: return null
      return if (loja == 4) {
        findImpressora(tipoRota.impressora)
      } else {
        val impressoras = allTermica()
        impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
      }
    }

    fun findImpressora(printerName: String): Impressora? {
      val listaImpressoras = all() + listOf(TODAS, ROTA, PISO, RESSU4)
      return listaImpressoras.firstOrNull { it.name.uppercase() == printerName.uppercase() }
    }

    val TODAS = Impressora(0, "Todas")
    val ROTA = Impressora(9999, "Rota")
    val PISO = Impressora(8888, "Piso")
    val RESSU4 = Impressora(7777, "Ressu4")
  }
}

enum class ETipoRota(val impressora: String) {
  ROTA("Exp4.Termica"), PISO("CD5A.Termica"), RESSU4("Ressu4.Termica")
}

fun Impressora.tipoRota(): ETipoRota? {
  return when (no) {
    9999 -> ETipoRota.ROTA
    8888 -> ETipoRota.PISO
    7777 -> ETipoRota.RESSU4
    else -> null
  }
}