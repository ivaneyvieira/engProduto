package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class EntradaDevCli(
  val invno: Int,
  var loja: Int,
  var notaFiscal: String?,
  var data: LocalDate?,
  var vendno: Int?,
  var fornecedor: String?,
  var remarks: String?,
  var varor: Double?,
  var storeno: Int?,
  var pdvno: Int?,
  var xano: Int?,
  var custno: Int?,
  var nfVenda: String?,
  var nfData: LocalDate?,
  var nfvaror: Double?,
  var cliente: String?,
  var empno: Int?,
  var vendedor: String?
) {

  fun produtos() = saci.entradaDevCliPro(invno)
  companion object {
    fun findAll(filtro: FiltroEntradaDevCli) = saci.entradaDevCli(filtro)
  }
}

data class FiltroEntradaDevCli(
  val loja: Int,
  val nota: String,
  val dataI: LocalDate?,
  val dataF: LocalDate?,
  val codigoCliente: Int,
  val nomeCliente: String,
) {
  fun isEmpty() = (nota == "") && (codigoCliente == 0) && (nomeCliente == "") && (dataI == null) && (dataF == null)

  val numero
    get() = nota.split("/").getOrNull(0) ?: ""
  val serie
    get() = nota.split("/").getOrNull(1) ?: ""
}