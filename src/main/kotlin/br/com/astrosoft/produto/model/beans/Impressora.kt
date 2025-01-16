package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.beans.ETipoRota.*
import br.com.astrosoft.produto.model.saci

class Impressora(var no: Int, var name: String) {
  companion object {
    fun all() = saci.findImpressoras()
    fun allTermica() = all().filter { it.name.contains("Termica", ignoreCase = true) }
    fun allEtiqueta() = all().filter { it.name.contains("Etiqueta", ignoreCase = true) }
    fun findImpressora(loja: Int?, tipoRota: ETipoRota): Impressora? {
      val impressoras = allTermica()
      loja ?: return null
      return when (tipoRota) {
        PISO, ROTA -> {
          if (loja == 4) {
            tipoRota.impressoraRota()
          } else {
            impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
          }
        }

        CONF3_EXP, CONF3_PISO -> {
          if (loja == 3) {
            tipoRota.impressoraRota()
          } else {
            impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
          }
        }

        CONF5_EXP, CONF5_PISO -> {
          if (loja == 5) {
            tipoRota.impressoraRota()
          } else {
            impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
          }
        }

        else -> null
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
  ROTA(numero = 9999, nome = "Exp.Exp", impressora = "Exp4.Termica"),
  PISO(numero = 8888, nome = "Exp.Piso", impressora = "CD5A.Termica"),
  RESSU4(numero = 7777, nome = "Exp.Ressu4", impressora = "Ressu4.Termica"),
  CONF5_EXP(numero = 6666, nome = "Conf5.Exp", impressora = "Conf5.Termica"),
  CONF5_PISO(numero = 5555, nome = "Conf5.Piso", impressora = "Conf5.Termica"),
  CONF3_EXP(numero = 4444, nome = "Conf3.Exp", impressora = "Conf3.Termica"),
  CONF3_PISO(numero = 3333, nome = "Conf3.Piso", impressora = "Conf3.Termica");

  fun impressora() = Impressora(numero, nome)
  fun impressoraRota() = Impressora(numero, impressora)

  fun impressoraLoja(loja: Int) = Impressora.findImpressora(loja, this)

  fun impressoraLojas(): List<Impressora> {
    val lojas = listOf(2, 3, 4, 5, 6, 7, 8)
    return lojas.mapNotNull { loja -> impressoraLoja(loja) }.distinctBy { it.name }.sortedBy { it.name }
  }

  companion object {
    fun impressoraLojas(): List<Impressora> {
      return entries.flatMap { it.impressoraLojas() }.distinctBy { it.name }.sortedBy { it.name }
    }
  }
}

fun Impressora.tipoRota(): ETipoRota? {
  return ETipoRota.entries.firstOrNull { it.numero == no }
}