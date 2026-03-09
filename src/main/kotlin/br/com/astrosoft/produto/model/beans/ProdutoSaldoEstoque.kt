package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoSaldoEstoque(
  var loja: Int?,
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var gradeProduto: String?,
  var unidade: String?,
  var qttyVarejo: Double?,
  var qttyAtacado: Double?,
  var qttyTotal: Double?,
  var custoVarejo: Double?,
  var custoTotal: Double?,
  var tributacao: String?,
  var rotulo: String?,
  var ncm: String?,
  var fornecedor: Int?,
  var abrev: String?,
  var tipo: Int?,
  var cl: Int?,
  var localizacao: String?,
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
  val grade: Boolean,
  val caracter: ECaracter,
  val letraDup: ELetraDup,
  val tipoSaldo: ETipoSaldo,
  val estoque: EEstoque,
  val saldo: Int,
  val consumo: EConsumo,
) {
  fun lojaSigla(): String {
    return saci.allLojas().firstOrNull { it.no == loja }?.sname ?: ""
  }
}
