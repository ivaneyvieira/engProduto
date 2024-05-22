package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoInventario(
  var prdno: String?,
  var codigo: String?,
  var descricao: String?,
  var grade: String?,
  var unidade: String?,
  var validade: Int?,
  var vendno: Int?,
  var fornecedorAbrev: String?,
  var estoqueTotal: Int?,
  var estoqueDS: Int?,
  var estoqueMR: Int?,
  var estoqueMF: Int?,
  var estoquePK: Int?,
  var estoqueTM: Int?,
  var vencimentoDS: Int?,
  var vencimentoMR: Int?,
  var vencimentoMF: Int?,
  var vencimentoPK: Int?,
  var vencimentoTM: Int?,
) {
  var vencimentoDSStr: String
    get() = vencimento(vencimentoDS)
    set(value) {
      vencimentoDS = mesAno(value)
    }

  var vencimentoMRStr: String
    get() = vencimento(vencimentoMR)
    set(value) {
      vencimentoMR = mesAno(value)
    }

  var vencimentoMFStr: String
    get() = vencimento(vencimentoMF)
    set(value) {
      vencimentoMF = mesAno(value)
    }

  var vencimentoPKStr: String
    get() = vencimento(vencimentoPK)
    set(value) {
      vencimentoPK = mesAno(value)
    }

  var vencimentoTMStr: String
    get() = vencimento(vencimentoTM)
    set(value) {
      vencimentoTM = mesAno(value)
    }

  fun mesAno(value: String): Int {
    val mes = value.substring(0, 2).toIntOrNull() ?: return 0
    val ano = value.substring(3, 5).toIntOrNull() ?: return 0
    return mes + (ano + 2000) * 100
  }

  fun vencimento(vencimento: Int?): String {
    vencimento ?: return ""
    val vencimentoStr = vencimento.toString()
    if (vencimentoStr.length != 6) return ""
    val mes = vencimentoStr.substring(4, 6)
    val ano = vencimentoStr.substring(2, 4)
    return "$mes/$ano"
  }

  fun update() {
    saci.updateProdutoValidade(this)
  }

  companion object {
    fun find(filtro: FiltroProdutoInventario): List<ProdutoInventario> {
      return saci.produtoValidade(filtro)
    }
  }
}

data class FiltroProdutoInventario(
  val pesquisa: String,
  val codigo: String,
  val validade: Int,
  val grade: String,
  val caracter: ECaracter,
)