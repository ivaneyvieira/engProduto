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
      val listaImpressoras = all() + ETipoRota.entries.map { it.impressora() }
      return listaImpressoras.firstOrNull { it.name.uppercase() == printerName.uppercase() }
    }
  }
}

enum class ETipoRota(val numero: Int, val nome: String, val impressora: String) {
  TODAS(numero = 0, nome = "Todas", impressora = ""),
  ROTA(numero = 9999, nome = "Rota", impressora = "Exp4.Termica"),
  PISO(numero = 8888, nome = "Piso", impressora = "CD5A.Termica"),
  RESSU4(numero = 7777, nome = "Ressu4", impressora = "Ressu4.Termica"),
  CONF5_EXP(numero = 6666, nome = "Conf5.Exp", impressora = "Conf5.Termica"),
  CONF5_PISO(numero = 5555, nome = "Conf5.Piso", impressora = "Conf5.Termica"),
  CONF3_EXP(numero = 4444, nome = "Conf3.Exp", impressora = "Conf3.Termica"),
  CONF3_PISO(numero = 3333, nome = "Conf3.Piso", impressora = "Conf3.Termica");

  fun impressora() = Impressora(numero, nome)
}

fun Impressora.tipoRota(): ETipoRota? {
  return ETipoRota.entries.firstOrNull { it.numero == no }
}