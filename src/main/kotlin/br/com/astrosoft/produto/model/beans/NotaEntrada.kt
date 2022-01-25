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
  val marca: Int,
  val cancelada: String,
  val chave: String,
                 ) {
  fun findProduto(marca: Boolean) = saci.findProdutoNFE(this, marca)

  fun produtosConferencia(): List<ProdutoNFE> {
    return saci.findProdutoNFEConf(this)
  }

  fun addProdutoConf(barcode : String) = saci.addProdutoConf(this, barcode)

  val nota
    get() = "$numero/$serie"

  companion object {
    fun findNotaEntrada(filtro: FiltroNotaEntrada) = saci.findNotaEntrada(filtro)

    fun marcaNotaEntrada(chave: String) = saci.marcaNotaEntrada(chave)
  }
}

data class FiltroNotaEntrada(val chave: String = "")