package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoSaldoAtacado(
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var gradeProduto: String?,
  var unidade: String?,
  var tipoValidade: String?,
  var mesesGarantia: Int?,
  var estoqueLojasAtacado: Int?,
  var custoLojasAtacado: Double?,
  var estoqueDSAtacado: Int?,
  var estoqueMRAtacado: Int?,
  var estoqueMFAtacado: Int?,
  var estoquePKAtacado: Int?,
  var estoqueTMAtacado: Int?,
  var custoDSAtacado: Double?,
  var custoMRAtacado: Double?,
  var custoMFAtacado: Double?,
  var custoPKAtacado: Double?,
  var custoTMAtacado: Double?,
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
    fun findProdutoSaldo(filtro: FiltroProdutoSaldoAtacado): List<ProdutoSaldoAtacado> {
      return saci.findProdutoSaldoAtacado(filtro)
    }
  }
}

data class FiltroProdutoSaldoAtacado(
  val pesquisa: String,
  val fornecedor: Int,
  val tributacao: String,
  val rotulo: String,
  val tipo: Int,
  val cl: Int,
  val caracter: ECaracter,
  val letraDup: ELetraDup,
  val grade: Boolean,
  val estoque: EEstoque,
  val saldo: Int,
  val consumo: EConsumo,
)