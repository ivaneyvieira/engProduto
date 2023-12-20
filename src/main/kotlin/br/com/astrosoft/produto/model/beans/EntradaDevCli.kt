package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class EntradaDevCli(
  val invno: Int,
  var loja: Int,
  var nomeLoja: String?,
  var notaFiscal: String?,
  var data: LocalDate?,
  var hora: String?,
  var vendno: Int?,
  var fornecedor: String?,
  var remarks: String?,
  var valor: Double?,
  var storeno: Int?,
  var pdvno: Int?,
  var xano: Int?,
  var custno: Int?,
  var nfVenda: String?,
  var nfData: LocalDate?,
  var nfValor: Double?,
  var cliente: String?,
  var cfo: Int?,
  var empno: Int?,
  var vendedor: String?,
  var impressora: String?,
  var userName: String?,
) {

  fun produtos() = saci.entradaDevCliPro(invno)
  fun marcaImpresso(impressora: Impressora) {
    saci.marcaImpresso(invno, impressora)
    val saldoDevolucao = SaldoDevolucao(invno, custno ?: 0, valor ?: 0.00)
    if (isReenbolso()) {
      saci.marcaImpressoReembolso(saldoDevolucao)
    } else if (custno == 200 || custno == 300 || custno == 400 || custno == 500 || custno == 800) {
      saci.saldoDevolucao(saldoDevolucao)
    }
  }

  fun isReenbolso(): Boolean {
    return remarks?.contains("EST CARTAO", ignoreCase = true) == true ||
           remarks?.contains("EST BOLETO", ignoreCase = true) == true ||
           remarks?.contains("REEMBOLSO", ignoreCase = true) == true
  }

  companion object {
    fun findAll(filtro: FiltroEntradaDevCli) = saci.entradaDevCli(filtro)
  }
}

data class FiltroEntradaDevCli(
  val loja: Int,
  val query: String,
  val dataI: LocalDate?,
  val dataF: LocalDate?,
  val dataLimiteInicial: LocalDate?,
  val impresso: Boolean?,
)