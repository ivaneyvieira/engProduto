package br.com.astrosoft.produto.model.nfeXml

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

  fun ajustaQuantidadeAvaria(quant: Double?) {
    valorTotalAvaria = quant?.times(valorUnitario) ?: 0.00
    valorIPIAvaria = valorTotalAvaria.times(aliqIPI / 100)
    aliqOutrosAvaria = (valorOutros ?: 0.00).times(100.00).div(valorTotalAvaria.plus(valorIPIAvaria))
    aliqFreteAvaria =
        (valorFrete ?: 0.00).times(100.00).div(valorTotalAvaria.plus(valorIPIAvaria).plus(valorOutros ?: 0.00))
    aliqDifICMS = (18.00).minus(aliqICMS)
    valorDifICMS =
        (valorTotalAvaria.plus(valorIPIAvaria).plus(valorOutros ?: 0.00).plus(valorFrete ?: 0.00))
          .times(aliqDifICMS)
          .div(100.00)
    valorUnitAvaria =
        valorTotalAvaria
          .plus(valorIPIAvaria)
          .plus(valorOutros ?: 0.00)
          .plus(valorFrete ?: 0.00)
          .plus(valorDifICMS)
          .div(quant ?: 1.00)
    valorTotalFinalAvaria = (quant ?: 0.00).times(valorUnitAvaria)
  }

  var valorTotalAvaria: Double = 0.00
  var valorIPIAvaria: Double = 0.00
  var aliqOutrosAvaria: Double = 0.00
  var aliqFreteAvaria: Double = 0.00
  var aliqDifICMS: Double = 0.00
  var valorDifICMS: Double = 0.00
  var valorUnitAvaria: Double = 0.00
  var valorTotalFinalAvaria: Double = 0.00
}