package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class LocalizaProduto {
  var loja: Int? = null
  var prdno: String? = null
  var grade: String? = null
  var barcode: String? = null
  var descricao: String? = null
  var codForn: Int? = null
  var locApp: String? = null
  var estoqueLoja: Int? = null

  companion object {
    fun findAll(filtro: FiltroLocalizaProduto): List<LocalizaProduto> {
      return if (filtro.vazio()) {
        emptyList()
      } else {
        saci.localizaProduto(filtro)
      }
    }
  }
}

data class FiltroLocalizaProduto(
  val loja: Int,
  val codForn: Int,
  var codPrd: String,
  val pesquisa: String,
  val tipo: Int,
  val cl: Int,
  val barcode: String,
) {
  fun vazio(): Boolean {
    return this.codPrd.isBlank() &&
           this.pesquisa.isBlank() &&
           this.barcode.isBlank() &&
           this.codForn == 0 &&
           this.cl == 0 &&
           this.tipo == 0 &&
           this.loja == 0
  }
}