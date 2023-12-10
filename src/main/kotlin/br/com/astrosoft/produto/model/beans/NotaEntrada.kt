package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaEntrada(
  var ni: Int,
  var loja: Int,
  var numero: String?,
  var serie: String?,
  var fornecedor: Int?,
  var nomeForn: String?,
  var emissao: LocalDate?,
  var entrada: LocalDate?,
  var valorNota: Double?,
  var cancelada: String?,
  var chave: String?,
  var marca: Int?,
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