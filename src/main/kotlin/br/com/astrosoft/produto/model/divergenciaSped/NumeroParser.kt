package br.com.astrosoft.produto.model.divergenciaSped

import java.math.BigDecimal

private const val NON_BREAKING_SPACE = '\u00A0'

fun parseDecimalOrZero(value: String): BigDecimal {
  return parseDecimalOrNull(value) ?: BigDecimal.ZERO
}

fun parseDecimalOrNull(value: String): BigDecimal? {
  val normalized = normalizeNumberString(value) ?: return null
  return try {
    BigDecimal(normalized)
  } catch (_: Exception) {
    null
  }
}

private fun normalizeNumberString(value: String): String? {
  val raw = value
    .replace(NON_BREAKING_SPACE.toString(), "")
    .replace(" ", "")
    .trim()

  if (raw.isEmpty()) return null

  val lastComma = raw.lastIndexOf(',')
  val lastDot = raw.lastIndexOf('.')

  return when {
    lastComma >= 0 && lastDot >= 0 -> {
      val decimalSeparator = if (lastComma > lastDot) ',' else '.'
      val groupingSeparator = if (decimalSeparator == ',') '.' else ','
      raw.replace(groupingSeparator.toString(), "").replace(decimalSeparator, '.')
    }

    lastComma >= 0 -> raw.replace(".", "").replace(',', '.')
    lastDot >= 0   -> normalizeDotOnly(raw)
    else           -> raw
  }
}

private fun normalizeDotOnly(value: String): String {
  val parts = value.split('.')
  if (parts.size <= 1) return value

  if (parts.size == 2 && parts[1].length != 3) {
    return value
  }

  return parts.joinToString("")
}
