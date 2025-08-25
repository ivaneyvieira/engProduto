package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class EntradaDevCliProList(
  var data: LocalDate?,
  var codLoja: Int?,
  var loja: String?,
  var prdno: String?,
  var codigo: String?,
  var descricao: String?,
  var grade: String?,
  var userName: String?,
  var userLogin: String?,
  var quantidade: Int?,
  var observacao: String?,
  var ni: Int?,
  var nota: String?,
  var valor: Double?,
  var tipo: String?,
  var tipoPrd: String?,
  var tipoQtd: Int?,
  var tipoQtdEfetiva: Int?,
) {
  fun tipoPrdTratado(): String {
    val temProduto = this.tipoPrd?.endsWith(" P") == true
    return tipoNotaPre() + if (temProduto) " P" else ""
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

  fun isTipoMisto(): Boolean {
    val tipoNota = this.tipo ?: ""
    return "TRO.* M.*".toRegex().matches(tipoNota) ||
           "EST.* M.*".toRegex().matches(tipoNota) ||
           "REE.* M.*".toRegex().matches(tipoNota)
  }

  val observacao01: String
    get() {
      val parte1 = observacao?.split(")")?.getOrNull(0) ?: return ""
      return "$parte1)"
    }

  val codigoFormat
    get() = codigo?.padStart(6, '0') ?: ""

  companion object {
    fun findAll(filtro: FiltroEntradaDevCliProList): List<EntradaDevCliProList> =
        saci.entradaDevCliProList(filtro).explodeMisto()

    fun findAll(listNi: List<Int>): List<EntradaDevCliProList> = saci.entradaDevCliProList(listNi).explodeMisto()
  }
}

data class FiltroEntradaDevCliProList(
  val loja: Int,
  val data: LocalDate,
  val pesquisa: String,
)

fun List<EntradaDevCliProList>.explodeMisto(): List<EntradaDevCliProList> {
  return this.flatMap { bean ->
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