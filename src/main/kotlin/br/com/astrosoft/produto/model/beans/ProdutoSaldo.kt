package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoSaldo(
  var loja: Int?,
  var prdno: String?,
  var codigo: String?,
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
) {
  companion object {
    fun findProdutoSaldo(filtro: FiltroProdutoSaldo): List<ProdutoSaldo> {
      return saci.findProdutoSaldo(filtro)
    }
  }
}

data class FiltroProdutoSaldo(
  val loja: Int,
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
  val update: Boolean,
) {
  fun lojaSigla(): String {
    return saci.allLojas().firstOrNull { it.no == loja }?.sname ?: ""
  }
}

enum class ECaracter(val value: String, val descricao: String) {
  SIM("S", "Sim"),
  NAO("N", "Não"),
  TODOS("T", "Todos"),
}

enum class ELetraDup(val value: String, val descricao: String) {
  SIM("S", "Sim"),
  NAO("N", "Não"),
  TODOS("T", "Todos"),
}

enum class EEstoque(val value: String, val descricao: String) {
  MENOR("<", "<"),
  MAIOR(">", ">"),
  IGUAL("=", "="),
  TODOS("T", "Todos"),
}