package br.com.astrosoft.framework.util

import java.util.Locale

private val PALAVRAS_MINUSCULAS = setOf(
  "a", "as",
  "o", "os",
  "de", "da", "das",
  "do", "dos",
  "e",
  "em",
  "na", "nas",
  "no", "nos",
  "para",
  "por",
  "com"
)

fun padronizarRazaoSocial(nome: String): String {
  val locale = Locale.forLanguageTag("pt-BR")

  return nome
    .trim()
    .split(Regex("\\s+"))
    .filter { it.isNotBlank() }
    .mapIndexed { indice, palavra ->
      formatarPalavra(
        palavra = palavra.lowercase(locale),
        primeiraPalavra = indice == 0,
        locale = locale
      )
    }
    .joinToString(" ")
}

private fun formatarPalavra(
  palavra: String,
  primeiraPalavra: Boolean,
  locale: Locale
): String {
  return palavra
    .split("-")
    .mapIndexed { indice, parte ->
      val primeiraParteDoNome = primeiraPalavra && indice == 0

      if (!primeiraParteDoNome && parte in PALAVRAS_MINUSCULAS) {
        parte
      } else {
        capitalizarPrimeiraLetra(parte, locale)
      }
    }
    .joinToString("-")
}

private fun capitalizarPrimeiraLetra(
  texto: String,
  locale: Locale
): String {
  val indicePrimeiraLetra = texto.indexOfFirst { it.isLetter() }

  if (indicePrimeiraLetra == -1) {
    return texto
  }

  return buildString {
    append(texto.substring(0, indicePrimeiraLetra))
    append(
      texto.substring(
        indicePrimeiraLetra,
        indicePrimeiraLetra + 1
      ).uppercase(locale)
    )
    append(texto.substring(indicePrimeiraLetra + 1))
  }
}

fun produzirNomeReduzido(
  razaoSocial: String,
  quantidadePalavrasPrincipais: Int = 2
): String {
  require(quantidadePalavrasPrincipais > 0) {
    "A quantidade de palavras deve ser maior que zero."
  }

  if (razaoSocial.isBlank()) {
    return ""
  }

  val locale = Locale.forLanguageTag("pt-BR")
  val nomePadronizado = padronizarRazaoSocial(razaoSocial)
  val palavras = nomePadronizado.split(Regex("\\s+"))

  val resultado = mutableListOf<String>()
  var palavrasPrincipaisEncontradas = 0

  for (palavra in palavras) {
    resultado += palavra

    val palavraNormalizada = palavra
      .lowercase(locale)
      .trim('.', ',', ';', ':', '(', ')')

    if (palavraNormalizada !in PALAVRAS_MINUSCULAS) {
      palavrasPrincipaisEncontradas++
    }

    if (
      palavrasPrincipaisEncontradas ==
      quantidadePalavrasPrincipais
    ) {
      break
    }
  }

  return resultado.joinToString(" ")
}