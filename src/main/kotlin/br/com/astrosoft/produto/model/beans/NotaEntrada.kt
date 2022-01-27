package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaEntrada(
  val ni: Int,
  val loja: Int,
  val numero: String,
  val serie: String,
  val fornecedor: Int,
  val nomeForn: String,
  val emissao: LocalDate?,
  val entrada: LocalDate?,
  val valorNota: Double,
  val cancelada: String,
  val chave: String,
                 ) {
  fun produtosPendente() = saci.findProdutoNFEPendente(this)

  fun produtosRecebido() = saci.findProdutoNFERecebido(this)

  fun produtosReceber(): List<ProdutoNFE> {
    return saci.findProdutoNFEReceber(this)
  }

  fun addProdutoReceber(barcode: String, quant: Int) = saci.addProdutoReceber(this, barcode, quant)

  val nota
    get() = "$numero/$serie"

  companion object {
    fun findNotaEntradaRecebido(filtro: FiltroNotaEntrada) = saci.findNotaEntradaRecebido(filtro)

    fun findNotaEntradaPendente(filtro: FiltroNotaEntrada) = saci.findNotaEntradaPendente(filtro)

    fun findNotaEntradaReceber(chave: String = "") = saci.findNotaEntradaReceber(chave)

    fun marcaNotaEntradaReceber(chave: String) = saci.marcaNotaEntradaReceber(chave)
  }
}

data class FiltroNotaEntrada(val loja: Int, val ni: Int, val nota: String, val vendno: Int) {
  val nfno: String
    get() = nota.split("/").getOrNull(0) ?: ""
  val nfse: String
    get() = nota.split("/").getOrNull(1) ?: ""
}