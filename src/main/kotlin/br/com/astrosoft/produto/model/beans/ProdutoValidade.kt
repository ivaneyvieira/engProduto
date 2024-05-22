package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoValidade(
  var prdno: String?,
  var codigo: String?,
  var descricao: String?,
  var grade: String?,
  var unidade: String?,
  var validade: Int?,
  var estoqueTotal: Int?,
  var estoqueDS: Int?,
  var estoqueMR: Int?,
  var estoqueMF: Int?,
  var estoquePK: Int?,
  var estoqueTM: Int?,
  var vencimentoDS: LocalDate?,
  var vencimentoMR: LocalDate?,
  var vencimentoMF: LocalDate?,
  var vencimentoPK: LocalDate?,
  var vencimentoTM: LocalDate?,
) {
  fun update() {
    saci.updateProdutoValidade(this)
  }

  companion object {
    fun find(filtro: FiltroProdutoValidade): List<ProdutoValidade> {
      return saci.produtoValidade(filtro)
    }
  }
}

data class FiltroProdutoValidade(
  val pesquisa: String,
  val codigo: String,
  val validade: Int,
  val grade: String,
  val caracter: ECaracter,
)