package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoSaldoEstoque(
  var loja: Int?,
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var gradeProduto: String?,
  var unidade: String?,
  var tipoValidade: String?,
  var mesesGarantia: Int?,
  var estoqueLojas: Int?,
  var qttyVarejo: Int?,
  var qttyAtacado: Int?,
  var qttyTotal: Int?,
  var tributacao: String?,
  var rotulo: String?,
  var ncm: String?,
  var fornecedor: Int?,
  var abrev: String?,
  var tipo: Int?,
  var cl: Int?,
  var localizacao: String?,
  var prdnoRel: String?,
  var codigoRel: Int?
) {
  val codigoStr
    get() = this.codigo?.toString() ?: ""

  companion object {
    fun findProdutoSaldoEstoque(filtro: FiltroProdutoSaldoEstoque): List<ProdutoSaldoEstoque> {
      return saci.findProdutoSaldoEstoque(filtro)
    }
  }
}

data class FiltroProdutoSaldoEstoque(
  val loja: Int,
  val ym: Int,
  val pesquisa: String,
  val fornecedor: Int,
  val tributacao: String,
  val rotulo: String,
  val tipo: Int,
  val cl: Int,
  val caracter: ECaracter,
  val letraDup: ELetraDup,
  val grade: Boolean,
  val tipoSaldo: ETipoSaldo,
  val estoque: EEstoque,
  val saldo: Int,
  val consumo: EConsumo,
  val update: Boolean,
) {
  fun lojaSigla(): String {
    return saci.allLojas().firstOrNull { it.no == loja }?.sname ?: ""
  }
}
