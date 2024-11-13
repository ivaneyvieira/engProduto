package br.com.astrosoft.produto.model.beans

import kotlin.math.absoluteValue

data class ProdutoNotaEntradaNdd(
  val id: Int,
  val numeroProtocolo: String,
  val codigo: String,
  val codBarra: String,
  val descricao: String,
  val ncm: String,
  val cst: String,
  val cfop: String,
  val un: String,
  val quantidade: Double,
  val valorUnitario: Double,
  val valorTotal: Double,
  val baseICMS: Double,
  val valorIPI: Double,
  val aliqICMS: Double,
  val aliqIPI: Double,
  val valorOutros: Double?,
  val valorFrete: Double?,
) {
  val temIPI
    get() = valorIPI.absoluteValue > 0.001

  val valorICMS
    get() = baseICMS * aliqICMS / 100
}