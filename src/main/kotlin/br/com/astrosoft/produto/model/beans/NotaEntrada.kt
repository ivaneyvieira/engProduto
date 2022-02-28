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
  val marca: Int,
                 ) {
  fun produtosRecebido() = saci.findProdutoNFERecebido(this)

  fun produtosBase() = saci.findProdutoNFEBase(this)

  fun produtosReceber(): List<ProdutoNFE> {
    return saci.findProdutoNFEReceber(this)
  }

  fun addProdutoReceber(barcode: String, quant: Int) = saci.addProdutoReceber(chave, barcode, quant)

  fun removeReceber() {
    saci.removerNotaReceber(this)
  }

  val nota
    get() = "$numero/$serie"

  companion object {
    fun findNotaEntradaBase(filtro: FiltroNotaEntrada) = saci.findNotaEntradaBase(filtro)

    fun findNotaEntradaRecebido(filtro: FiltroNotaEntrada) = saci.findNotaEntradaRecebido(filtro)

    fun findNotaEntradaReceber(chave: String = "") = saci.findNotaEntradaReceber(chave)

    fun marcaNotaEntradaReceber(chave: String, marca: Int) = saci.marcaNotaEntradaReceber(chave, marca)
  }
}

data class FiltroNotaEntrada(val loja: Int, val ni: Int, val nota: String, val vendno: Int, val chave: String) {
  val nfno: String
    get() = nota.split("/").getOrNull(0) ?: ""
  val nfse: String
    get() = nota.split("/").getOrNull(1) ?: ""
}