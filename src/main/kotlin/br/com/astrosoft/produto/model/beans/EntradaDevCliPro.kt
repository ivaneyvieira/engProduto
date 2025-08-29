package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

data class EntradaDevCliPro(
  var invno: Int,
  var codLoja: Int,
  var loja: String,
  var notaFiscal: String?,
  var data: String?,
  var vendno: Int?,
  var remarks: String?,
  var storeno: Int?,
  var pdvno: Int?,
  var xano: Int?,
  var nfVenda: String?,
  var nfData: String?,
  var custno: Int?,
  var empno: Int?,
  var fornecedor: String?,
  var prdno: String?,
  var codigo: String?,
  var grade: String?,
  var descricao: String?,
  var quantidade: Int?,
  var valorUnitario: Double?,
  var valorTotal: Double?,
  var cliente: String?,
  var vendedor: String?,
  var tipo: String?,
  var tipoPrd: String?,
  var tipoQtd: Int?,
  var tipoQtdEfetiva: Int?,
) {
  fun tipoPrdTratado(): String {
    val temProduto = this.tipoPrd?.endsWith(" P") == true
    return tipoNotaPre() + if (temProduto) " P" else ""
  }

  fun marcaAjuste(ajuste: AjusteProduto) {
    saci.marcaAjuste(this, ajuste)
  }

  val codigoFormat
    get() = codigo?.padStart(6, '0') ?: ""

  fun isTipoMisto(): Boolean {
    val tipoNota = this.tipo ?: ""
    return "TRO.* M.*".toRegex().matches(tipoNota) ||
           "EST.* M.*".toRegex().matches(tipoNota) ||
           "REE.* M.*".toRegex().matches(tipoNota)
  }

  fun tipoNotaPre(): String {
    val tipoNota = this.tipo ?: return ""
    return when {
      "TRO.*".toRegex().matches(tipoNota) -> "TROCA"
      "EST.*".toRegex().matches(tipoNota) -> "ESTORNO"
      "REE.*".toRegex().matches(tipoNota) -> "REEMBOLSO"
      else                                -> tipoNota
    }
  }
}

fun List<EntradaDevCliPro>.explodeMisto(): List<EntradaDevCliPro> {
  return this.flatMap { bean ->
    if ((bean.tipoPrd ?: "") == "") {
      return@flatMap listOf(bean)
    }

    if (!(bean.tipoPrd ?: "").endsWith(" P")) {
      return@flatMap listOf(
        bean.copy(
          tipoPrd = bean.tipoNotaPre(),
          tipoQtd = 0,
          tipoQtdEfetiva = (bean.quantidade ?: 0)
        )
      )
    }

    val quantComProduto = (bean.tipoQtd ?: 0)
    val quantSemProduto = (bean.quantidade ?: 0) - (bean.tipoQtd ?: 0)
    val itemsComProdutos = if (quantComProduto == 0) {
      null
    } else {
      bean.copy(
        tipoPrd = "${bean.tipoNotaPre()} P",
        tipoQtd = quantComProduto,
        tipoQtdEfetiva = quantComProduto
      )
    }
    val itemsSemProdutos = if (quantSemProduto == 0) {
      null
    } else {
      bean.copy(
        tipoPrd = bean.tipoNotaPre(),
        tipoQtd = quantSemProduto,
        tipoQtdEfetiva = quantSemProduto
      )
    }
    listOfNotNull(itemsComProdutos, itemsSemProdutos)
  }
}