package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoSaldo(
  val loja: Int?,
  val prdno: String?,
  val codigo: String?,
  val descricao: String?,
  val gradeProduto: String?,
  val unidade: String?,
  val estoqueLojas: Int?,
  val qttyVarejo: Int?,
  val qttyAtacado: Int?,
  val qttyTotal: Int?,
  val tributacao: String?,
  val rotulo: String?,
  val ncm: String?,
  val fornecedor: Int?,
  val abrev: String?,
  val tipo: Int?,
  val cl: Int?,
) {
  companion object {
    fun findProdutoSaldo(filtro: FiltroProdutoSaldo): List<ProdutoSaldo> {
      return saci.findProdutoSaldo(filtro)
    }
  }
}

data class FiltroProdutoSaldo(
  val loja: Int,
  val fornecedor: Int,
  val tributacao: String,
  val rotulo: String,
  val tipo: Int,
  val cl: Int,
  val caracter: ECaracter,
  val grade: Boolean,
  val estoque: EEstoque,
  val saldo: Int,
)

enum class ECaracter(val value: String, val descricao: String) {
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