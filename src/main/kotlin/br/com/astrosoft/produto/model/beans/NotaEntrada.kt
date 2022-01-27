package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaEntrada(
  val ni: Int,
  val loja: Int,
  val numero: String,
  val serie: String,
  val fornecedor: Int,
  val data: LocalDate,
  val valorNota: Double,
  val cancelada: String,
  val chave: String,
                 ) {
  fun produtosPendente() = saci.findProdutoNFEPendente(this)

  fun produtosRecebido() = saci.findProdutoNFERecebido(this)

  fun produtosReceber(): List<ProdutoNFE> {
    return saci.findProdutoNFEReceber(this)
  }

  fun addProdutoReceber(barcode : String, quant : Int) = saci.addProdutoReceber(this, barcode, quant)

  val nota
    get() = "$numero/$serie"

  companion object {
    fun findNotaEntradaRecebido() = saci.findNotaEntradaRecebido()

    fun findNotaEntradaPendente(filtro: FiltroNotaEntrada) = saci.findNotaEntradaPendente(filtro)

    fun findNotaEntradaReceber(filtro: FiltroNotaEntrada) = saci.findNotaEntradaReceber(filtro)

    fun marcaNotaEntradaReceber(chave: String) = saci.marcaNotaEntradaReceber(chave)
  }
}

data class FiltroNotaEntrada(val chave: String = "")