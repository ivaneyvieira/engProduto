package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoCadastro {
  var prdno: String? = null
  var codigo: String? = null
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
  var lojas: String? = null

  val configSt
    get() = if(ctLoja == 0) "N" else "S"

  fun updateSt() {
    prdno?.let { pno ->
      saci.updateProdutoSt(pno)
    }
  }

  companion object {
    fun find(filtro: FiltroProdutoCadastro): List<ProdutoCadastro> {
      return saci.findProdutoCadastro(filtro)
    }
  }
}

data class FiltroProdutoCadastro(
  val pesquisa: String,
  val vendno: Int,
  val taxno: String,
  val typeno: Int,
  val clno: Int,
  val rotulo: String,
  val caracter: ECaracter,
  val letraDup: ELetraDup,
  val estoque: EEstoque,
  val saldo: Int,
  val configSt: Boolean,
)