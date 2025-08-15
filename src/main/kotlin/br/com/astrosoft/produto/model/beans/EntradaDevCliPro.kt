package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class EntradaDevCliPro(
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
) {
  fun marcaAjuste(ajuste: AjusteProduto) {
    saci.marcaAjuste(this, ajuste)
  }

  val codigoFormat
    get() = codigo?.padStart(6, '0') ?: ""

  fun isTipoMisto(): Boolean {
    val tipoNota = this.tipo ?: ""
    return tipoNota.startsWith("TROCA M") ||
           tipoNota.startsWith("ESTORNO M") ||
           tipoNota.startsWith("REEMBOLSO M")
  }
}
