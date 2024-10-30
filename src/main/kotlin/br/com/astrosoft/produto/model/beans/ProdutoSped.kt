package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoSped {
  var prdno: String? = null
  var codigo: Int? = null
  var descricao: String? = null
  var unidade: String? = null
  var rotulo: String? = null
  var tributacao: String? = null
  var forn: String? = null
  var abrev: String? = null
  var ncm: String? = null
  var tipo: Int? = null
  var clno: Int? = null
  var refForn: String? = null
  var pesoBruto: Double? = null
  var uGar: String? = null
  var tGar: String? = null
  var emb: Double? = null
  var foraLinha: String? = null
  var saldo: Int? = null
  var ctLoja: Int? = null
  var ctIpi: Int? = null
  var ctPis: Int? = null
  var ctIcms: Int? = null
  var ctErroPisCofins: Int? = null
  var ctErroRotulo: Int? = null
  var lojas: String? = null

  val pisCofOk: String
    get() = if (ctErroPisCofins == 0) "S" else "N"

  val rotuloOk: String
    get() = if (ctErroRotulo == 0) "S" else "N"

  val configSt
    get() = if (ctLoja == 0) "N" else "S"

  fun updateSt() {
    prdno?.let { pno ->
      saci.updateProdutoSt(pno)
    }
  }

  companion object {
    fun find(filtro: FiltroProdutoSped): List<ProdutoSped> {
      return saci.findProdutoSped(filtro)
    }
  }
}

data class FiltroProdutoSped(
  val pesquisa: String,
  val vendno: Int,
  val taxno: String,
  val typeno: Int,
  val clno: Int,
  val rotulo: String,
  val caracter: ECaracter,
  val letraDup: ELetraDup,
  val configSt: Boolean,
  val pisCofN: Boolean,
  val rotuloN: Boolean,
  val consumo: EConsumo
)