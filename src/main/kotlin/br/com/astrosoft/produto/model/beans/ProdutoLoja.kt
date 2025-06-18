package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoLoja(
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var gradeProduto: String?,
  var unidade: String?,
  var ncm: String?,
  var fornecedor: Int?,
  var tributacao: String?,
  var rotulo: String?,
  var tipo: Int?,
  var cl: Int?,
  var localizacao: String?,
  var prdnoRel: String?,
  var codigoRel: Int?,
  var estoqueTotal: Int?,
  var estoqueDS: Int?,
  var estoqueMR: Int?,
  var estoqueMF: Int?,
  var estoquePK: Int?,
  var estoqueTM: Int?,
) {
  val codigoStr
    get() = this.codigo?.toString() ?: ""

  companion object {
    fun findProdutoLoja(filtro: FiltroProdutoLoja): List<ProdutoLoja> {
      return saci.findProdutoLoja(filtro)
    }
  }
}

data class FiltroProdutoLoja(
  val pesquisa: String,
  val fornecedor: Int,
  val tipo: Int,
  val cl: Int,
  val caracter: ECaracter,
  val letraDup: ELetraDup,
  val grade: Boolean,
  val estoque: EEstoque,
  val saldo: Int,
  val consumo: EConsumo,
  val ncm: String,
  val tributacao: String,
  val rotulo: String,
)

