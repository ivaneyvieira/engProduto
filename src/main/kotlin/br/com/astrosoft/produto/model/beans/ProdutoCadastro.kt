package br.com.astrosoft.produto.model.beans

class ProdutoCadastro {
  var prdno: String? = null
  var codigo: String? = null
  var descricao: String? = null
  var grade: String? = null
  var unidade: String? = null
  var rotulo: String? = null
  var tributacao: String? = null
  var forn: String? = null
  var abrev: String? = null
  var ncm: String? = null
  var tipo: Int? = null
  var clno: Int? = null
  var barcode: String? = null
  var refForn: String? = null
  var pesoBruto: Double? = null
  var uGar: String? = null
  var tGar: String? = null
  var emb: Double? = null
  var foraLinha: String? = null
  var saldo: Int? = null
}

data class FiltroProdutoCadastro(
  val pesquisa: String,
  val vendno: Int,
  val taxno: String,
  val typeno: Int,
  val clno: Int,
  val caracter: ECaracter,
  val letraDup: ELetraDup,
  val grade: Boolean,
  val estoque: EEstoque,
  val saldo: Int,
)