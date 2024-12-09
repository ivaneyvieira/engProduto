package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class LocalizacaoAlternativa {
  var prdno: String? = null
  var loc: String? = null
  var clno: Int? = null
  var vendno: Int? = null
  var qtde: Int? = null

  companion object {
    val list = mutableListOf<LocalizacaoAlternativa>()
    val clList = mutableListOf<LocalizacaoCl>()
    val fornList = mutableListOf<LocalizacaoForn>()

    fun update() {
      list.clear()
      list.addAll(saci.localizacaoAlternativa())
      updateCl()
      updateForn()
    }

    private fun updateCl() {
      val map = list.groupBy { it.clno }
      val locs = map.map { (clno, list) ->
        val locs = list.mapNotNull { it.loc }.distinct()
        LocalizacaoCl(clno ?: 0, locs)
      }
      clList.clear()
      clList.addAll(locs)
    }

    private fun updateForn() {
      val map = list.groupBy { it.vendno }
      val locs = map.map { (vendno, list) ->
        val locs = list.mapNotNull { it.loc }.distinct()
        LocalizacaoForn(vendno ?: 0, locs)
      }
      fornList.clear()
      fornList.addAll(locs)
    }

    fun locsCl(clno: Int): String? {
      return clList.firstOrNull { it.clno == clno }?.locs?.maxOrNull()
    }

    fun locsForn(vendno: Int): String? {
      return fornList.firstOrNull { it.vendno == vendno }?.locs?.maxOrNull()
    }
  }
}

data class LocalizacaoCl(val clno: Int, val locs: List<String>)

data class LocalizacaoForn(val vendno: Int, val locs: List<String>)

