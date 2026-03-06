package br.com.astrosoft.produto.model.divergenciaSped

import java.math.BigDecimal

data class DivergenciaInventario(
    val codigo: String,
    val quantidadeExcel: BigDecimal?,
    val quantidadeSped: BigDecimal?,
    val valorUnitExcel: BigDecimal?,
    val valorUnitSped: BigDecimal?,
    val tipo: TipoDivergencia
)

enum class TipoDivergencia {
    SOMENTE_EXCEL,
    SOMENTE_SPED,
    DIFERENTE
}

fun valoresIguaisComTolerancia(
  valorA: BigDecimal?,
  valorB: BigDecimal?,
  tolerancia: BigDecimal = BigDecimal("0.01")
): Boolean {
  if (valorA == null || valorB == null) return valorA == valorB
  return valorA.subtract(valorB).abs() <= tolerancia
}
