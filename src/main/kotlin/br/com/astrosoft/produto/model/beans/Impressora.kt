package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import kotlin.collections.plus

class Impressora(var no: Int, var name: String) {
  companion object {
    private const val CACHE_TTL_MILLIS = 5 * 60 * 1000L

    @Volatile
    private var impressoraCache: List<Impressora> = emptyList()

    @Volatile
    private var lastRefreshMillis: Long = 0L

    private fun isCacheExpired(nowMillis: Long = System.currentTimeMillis()): Boolean {
      return nowMillis - lastRefreshMillis > CACHE_TTL_MILLIS
    }

    private fun refreshAll(): List<Impressora> {
      val list = saci.findImpressoras()
      synchronized(this) {
        impressoraCache = list
        lastRefreshMillis = System.currentTimeMillis()
        return impressoraCache
      }
    }

    fun invalidateCache() {
      synchronized(this) {
        impressoraCache = emptyList()
        lastRefreshMillis = 0L
      }
    }

    fun all(): List<Impressora> {
      return if (impressoraCache.isEmpty() || isCacheExpired()) {
        refreshAll()
      } else {
        impressoraCache
      }
    }

    fun allTermica() = all().filter { it.name.contains("Termica", ignoreCase = true) }
    fun allEtiqueta() = all().filter { it.name.contains("Etiqueta", ignoreCase = true) }
    fun findImpressoraOrigem(loja: Int?, tipoRota: ETipoRota): Impressora? {
      val impressoras = allTermica()
      loja ?: return null
      return when (tipoRota) {
        ETipoRota.PISO, ETipoRota.ROTA            -> {
          if (loja == 4) {
            tipoRota.impressoraRota()
          } else {
            impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
          }
        }

        ETipoRota.CONF3_EXP, ETipoRota.CONF3_PISO -> {
          if (loja == 3) {
            tipoRota.impressoraRota()
          } else {
            impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
          }
        }

        ETipoRota.CONF5_EXP, ETipoRota.CONF5_PISO -> {
          if (loja == 5) {
            tipoRota.impressoraRota()
          } else {
            impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
          }
        }

        ETipoRota.CONF_EXP                        -> {
          impressoras.firstOrNull { it.name.contains("conf$loja", ignoreCase = true) }
        }

        ETipoRota.EXP_CONF                        -> {
          impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
        }

        else                                      -> null
      }
    }

    fun findImpressoraDestino(loja: Int?, tipoRota: ETipoRota): Impressora? {
      val impressoras = allTermica()
      loja ?: return null
      return when (tipoRota) {
        ETipoRota.PISO, ETipoRota.ROTA            -> {
          if (loja == 4) {
            tipoRota.impressoraRota()
          } else {
            impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
          }
        }

        ETipoRota.CONF3_EXP, ETipoRota.CONF3_PISO -> {
          if (loja == 3) {
            tipoRota.impressoraRota()
          } else {
            impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
          }
        }

        ETipoRota.CONF5_EXP, ETipoRota.CONF5_PISO -> {
          if (loja == 5) {
            tipoRota.impressoraRota()
          } else {
            impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
          }
        }

        ETipoRota.CONF_EXP                        -> {
          impressoras.firstOrNull { it.name.contains("exp$loja", ignoreCase = true) }
        }

        ETipoRota.EXP_CONF                        -> {
          impressoras.firstOrNull { it.name.contains("conf$loja", ignoreCase = true) }
        }

        else                                      -> null
      }
    }

    fun findImpressora(printerName: String): Impressora? {
      val listaImpressoras = all() + ETipoRota.entries.map { it.impressora() }
      return listaImpressoras.firstOrNull { it.name.equals(printerName, ignoreCase = true) }
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
  CONF3_PISO(numero = 3333, nome = "Conf3.Piso", impressora = "Conf3.Termica"),
  CONF_EXP(numero = 2233, nome = "Conf.Exp", impressora = "Conf4.Termica"),
  EXP_CONF(numero = 3322, nome = "Exp.Conf", impressora = "Exp4.Termica");

  fun impressora() = Impressora(numero, nome)
  fun impressoraRota() = Impressora(numero, impressora)

  fun impressoraLojaOrigem(loja: Int) = Impressora.findImpressoraOrigem(loja, this)
  fun impressoraLojaDestino(loja: Int) = Impressora.findImpressoraDestino(loja, this)

  fun impressoraLojasOrigem(): List<Impressora> {
    val lojas = listOf(2, 3, 4, 5, 6, 7, 8)
    return lojas.mapNotNull { loja -> impressoraLojaOrigem(loja) }.distinctBy { it.name }.sortedBy { it.name }
  }

  fun impressoraLojasDestino(): List<Impressora> {
    val lojas = listOf(2, 3, 4, 5, 6, 7, 8)
    return lojas.mapNotNull { loja -> impressoraLojaDestino(loja) }.distinctBy { it.name }.sortedBy { it.name }
  }

  companion object {
    fun impressoraLojas(): List<Impressora> {
      val listaOrigem = entries.flatMap { it.impressoraLojasOrigem() }
      val listaDestino = entries.flatMap { it.impressoraLojasDestino() }
      val listaRota =  ETipoRota.entries.map { it.impressora() }
      return (listaOrigem + listaDestino + listaRota).distinctBy { it.name }
    }
  }
}

fun Impressora.tipoRota(): ETipoRota? {
  return ETipoRota.entries.firstOrNull { it.numero == no }
}
