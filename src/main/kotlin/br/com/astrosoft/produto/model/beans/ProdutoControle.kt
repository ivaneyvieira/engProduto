package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import kotlin.collections.forEach

class ProdutoControle(
  var loja: Int?,
  var lojaSigla: String?,
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var unidade: String?,
  var grade: String?,
  var tipo: Int?,
  var cl: Int?,
  var embalagem: Int?,
  var qtdEmbalagem: Double?,
  var locNerus: String?,
  var codForn: Int?,
  var fornecedor: String?,
  var fornecedorAbrev: String?,
  var cnpjFornecedor: String?,
  var saldo: Int?,
  var valorEstoque: Double?,
  var saldoVarejo: Int?,
  var saldoAtacado: Int?,
  var dataInicial: LocalDate?,
  var estoqueLoja: Int? = null,
  var preco: Double? = null,
  var barcode: String? = null,
  var ref: String? = null,
) {
  val codigoStr
    get() = this.codigo?.toString() ?: ""

  fun findKardec(dataInicial: LocalDate?): List<ControleKardec> {
    val vendas = saci.findProdutoKardec(
      loja = loja ?: return emptyList(),
      prdno = prdno ?: return emptyList(),
      grade = grade ?: return emptyList(),
      dataInicial = dataInicial
    )

    val result = saldoInicial(dataInicial) + vendas
    var saldoTotal = 0
    result.forEach { kad ->
      saldoTotal += (kad.qtde ?: 0)
      kad.saldo = saldoTotal
    }
    return result
  }

  private fun saldoInicial(dataInicial: LocalDate?): List<ControleKardec> {
    return listOf(
      ControleKardec(
        loja = loja,
        prdno = prdno,
        grade = grade,
        data = dataInicial,
        doc = "",
        tipo = ETipoKardecControle.INICIAL,
        qtde = estoqueLoja,
        saldo = estoqueLoja
      )
    )
  }

  fun updateControle() {
    saci.updateControle(this)
  }

  companion object {
    fun findProdutoControle(filter: FiltroProdutoControle): List<ProdutoControle> {
      return saci.findProdutoControle(filter)
    }
  }
}

data class FiltroProdutoControle(
  val loja: Int,
  val pesquisa: String,
  val codigo: Int,
  val grade: String,
  val caracter: ECaracter,
  val fornecedor: String,
  val centroLucro: Int,
  val estoque: EEstoque,
  val saldo: Int,
  val inativo: EInativo,
  val letraDup: ELetraDup,
  val cl: Int,
) {
  val prdno = if (codigo == 0) "" else codigo.toString().lpad(16, " ")
}