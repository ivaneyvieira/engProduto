package br.com.astrosoft.produto.model.beans

import java.io.File
import java.math.BigDecimal

data class ArquivoSped(
  val codItem: String,
  val unidade: String,
  val quantidade: Double,
  val valorUnitario: Double,
  val valorItem: Double,
  val indicadorPropriedade: Double
)

fun lerRegistrosH010(caminhoArquivo: String): List<ArquivoSped> {

  val registros = mutableListOf<ArquivoSped>()

  File(caminhoArquivo).forEachLine { linha ->

    if (!linha.startsWith("|H010|")) return@forEachLine

    val campos = linha.split("|")

    if (campos.size < 8) return@forEachLine

    val registro = ArquivoSped(
      codItem = campos[2],
      unidade = campos[3],
      quantidade = campos[4].toBigDecimalOrZero().toDouble(),
      valorUnitario = campos[5].toBigDecimalOrZero().toDouble(),
      valorItem = campos[6].toBigDecimalOrZero().toDouble(),
      indicadorPropriedade = campos[7].toBigDecimalOrZero().toDouble()
    )

    registros.add(registro)
  }

  return registros
}

fun String.toBigDecimalOrZero(): BigDecimal {
  return try {
    this.replace(",", ".")
      .trim()
      .toBigDecimal()
  } catch (e: Exception) {
    BigDecimal.ZERO
  }
}