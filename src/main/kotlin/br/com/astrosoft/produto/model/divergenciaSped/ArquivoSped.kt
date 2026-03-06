package br.com.astrosoft.produto.model.divergenciaSped

import java.io.File
import java.math.BigDecimal

data class ArquivoSped(
  val codItem: String,
  val unidade: String,
  val quantidade: BigDecimal,
  val valorUnitario: BigDecimal,
  val valorItem: BigDecimal,
  val indicadorPropriedade: BigDecimal
)

data class SpedParseWarning(
  val linhaNumero: Int,
  val linha: String,
  val motivo: String
)

fun lerRegistrosH010(
  caminhoArquivo: String,
  onAviso: ((SpedParseWarning) -> Unit)? = null
): List<ArquivoSped> {
  val registros = mutableListOf<ArquivoSped>()
  var linhaNumero = 0

  File(caminhoArquivo).forEachLine { linha ->
    linhaNumero += 1

    if (!linha.startsWith("|H010|")) return@forEachLine

    val campos = linha.split("|")

    if (campos.size < 8) {
      onAviso?.invoke(
        SpedParseWarning(
          linhaNumero = linhaNumero,
          linha = linha,
          motivo = "Campos insuficientes: ${campos.size}"
        )
      )
      return@forEachLine
    }

    val quantidade = parseCampo(campos[4], "quantidade", linhaNumero, linha, onAviso)
    val valorUnitario = parseCampo(campos[5], "valorUnitario", linhaNumero, linha, onAviso)
    val valorItem = parseCampo(campos[6], "valorItem", linhaNumero, linha, onAviso)
    val indicadorPropriedade = parseCampo(campos[7], "indicadorPropriedade", linhaNumero, linha, onAviso)

    val registro = ArquivoSped(
      codItem = campos[2],
      unidade = campos[3],
      quantidade = quantidade,
      valorUnitario = valorUnitario,
      valorItem = valorItem,
      indicadorPropriedade = indicadorPropriedade
    )

    registros.add(registro)
  }

  return registros
}

private fun parseCampo(
  valor: String,
  nomeCampo: String,
  linhaNumero: Int,
  linha: String,
  onAviso: ((SpedParseWarning) -> Unit)?
): BigDecimal {
  val parsed = parseDecimalOrNull(valor)
  if (parsed == null && valor.isNotBlank()) {
    onAviso?.invoke(
      SpedParseWarning(
        linhaNumero = linhaNumero,
        linha = linha,
        motivo = "Campo $nomeCampo invalido: '$valor'"
      )
    )
  }
  return parsed ?: BigDecimal.ZERO
}
