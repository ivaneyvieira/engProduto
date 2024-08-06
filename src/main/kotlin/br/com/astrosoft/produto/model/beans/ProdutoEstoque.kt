package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoEstoque(
  var loja: Int?,
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var unidade: String?,
  var grade: String?,
  var embalagem: Int?,
  var qtdEmbalagem: Int?,
  var estoque: Int?,
  var locSaci: String?,
  var locApp: String?,
  var codForn: Int,
  var fornecedor: String,
  var saldo: Int?,
) {
  fun update() {
    saci.updateProdutoEstoque(this)
  }

  fun recebimentos(): List<ProdutoKardec> {
    val filtro = FiltroNotaRecebimentoProduto(
      loja = 0,
      pesquisa = "",
      marca = EMarcaRecebimento.RECEBIDO,
      dataFinal = null,
      dataInicial = LocalDate.of(2024, 8, 1),
      localizacao = listOf("TODOS"),
      prdno = prdno ?: "",
      grade = grade ?: "SEM GRADE"
    )
    return saci.findNotaRecebimentoProduto(filtro).mapNotNull { nota ->
      ProdutoKardec(
        loja = nota.loja ?: return@mapNotNull null,
        prdno = prdno ?: "",
        grade = grade ?: "",
        data = nota.data ?: return@mapNotNull null,
        doc = nota.nfEntrada?: "",
        tipo = "Recebimento",
        qtde = nota.quant ?: 0,
        saldo = 0
      )
    }
  }

  companion object {
    fun findProdutoEstoque(filter: FiltroProdutoEstoque): List<ProdutoEstoque> {
      return saci.findProdutoEstoque(filter)
    }
  }
}

data class FiltroProdutoEstoque(
  val loja: Int,
  val pesquisa: String,
  val codigo: Int,
  val grade: String,
  val caracter: ECaracter,
  val localizacao: String?,
  val fornecedor: String,
)