package br.com.astrosoft.framework.util

import java.util.*

private val LOCALE_PT_BR: Locale = Locale.forLanguageTag("pt-BR")

private val PALAVRAS_NAO_CONTABILIZADAS = setOf(
  // Artigos
  "a",
  "as",
  "o",
  "os",
  "um",
  "uma",
  "uns",
  "umas",
  
  // Preposições
  "ante",
  "após",
  "até",
  "com",
  "contra",
  "de",
  "desde",
  "em",
  "entre",
  "para",
  "perante",
  "por",
  "sem",
  "sob",
  "sobre",
  "trás",
  
  // Contrações
  "ao",
  "aos",
  "à",
  "às",
  "da",
  "das",
  "do",
  "dos",
  "na",
  "nas",
  "no",
  "nos",
  "pela",
  "pelas",
  "pelo",
  "pelos",
  "num",
  "numa",
  "nuns",
  "numas",
  "dum",
  "duma",
  
  // Conjunções
  "e",
  "ou",
  "que"
)

/*
 * A chave não possui pontos, barras ou outros caracteres.
 *
 * Exemplos:
 * S.A. -> SA
 * S/A  -> SA
 * Ltda. -> LTDA
 */
private val SIGLAS_EMPRESARIAIS_PADRAO = mapOf(
  "LTDA" to "LTDA",
  "SA" to "S.A.",
  "ME" to "ME",
  "EPP" to "EPP",
  "MEI" to "MEI",
  "EIRELI" to "EIRELI",
  "SLU" to "SLU",
  "EI" to "EI",
  "SPE" to "SPE",
  "SCP" to "SCP",
  "SC" to "S/C",
  "SS" to "S/S",
  "CIA" to "CIA"
)

fun padronizarRazaoSocial(razaoSocial: String, siglasAdicionais: Set<String> = emptySet()): String {
  if (razaoSocial.isBlank()) {
    return ""
  }
  
  val siglas = criarMapaDeSiglas(siglasAdicionais)
  
  return razaoSocial.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.mapIndexed { indice, palavra ->
      formatarPalavra(
        palavra = palavra, primeiraPalavra = indice == 0, siglas = siglas
      )
  }.joinToString(" ")
}

fun produzirNomeReduzido(
  razaoSocial: String, quantidadePalavrasPrincipais: Int = 2, siglasAdicionais: Set<String> = emptySet()
): String {
  require(quantidadePalavrasPrincipais > 0) {
    "A quantidade de palavras deve ser maior que zero."
  }
  
  if (razaoSocial.isBlank()) {
    return ""
  }
  
  val siglas = criarMapaDeSiglas(siglasAdicionais)
  
  val nomePadronizado = padronizarRazaoSocial(
    razaoSocial = razaoSocial, siglasAdicionais = siglasAdicionais
  )
  
  val resultado = mutableListOf<String>()
  var palavrasPrincipaisEncontradas = 0
  
  for (palavra in nomePadronizado.split(Regex("\\s+"))) {
    resultado += palavra
    
    val chave = criarChaveDeSigla(palavra)
    val palavraNormalizada = normalizarParaComparacao(palavra)
    
    val ehSigla = chave in siglas
    val ehPalavraDeLigacao = palavraNormalizada in PALAVRAS_NAO_CONTABILIZADAS
    
    /*
     * As siglas sempre são contabilizadas.
     * Artigos, preposições e conjunções não são contabilizados.
     */
    if (ehSigla || !ehPalavraDeLigacao) {
      palavrasPrincipaisEncontradas++
    }
    
    if (palavrasPrincipaisEncontradas >= quantidadePalavrasPrincipais) {
      break
    }
  }
  
  return resultado.joinToString(" ")
}

private fun criarMapaDeSiglas(
  siglasAdicionais: Set<String>
): Map<String, String> {
  val adicionais = siglasAdicionais.associate { sigla ->
    criarChaveDeSigla(sigla) to sigla.uppercase(LOCALE_PT_BR)
  }
  
  return SIGLAS_EMPRESARIAIS_PADRAO + adicionais
}

private fun formatarPalavra(
  palavra: String, primeiraPalavra: Boolean, siglas: Map<String, String>
): String {
  val chave = criarChaveDeSigla(palavra)
  val siglaPadronizada = siglas[chave]
  
  if (siglaPadronizada != null) {
    return siglaPadronizada
  }
  
  return palavra.lowercase(LOCALE_PT_BR).split("-").mapIndexed { indice, parte ->
    val primeiraParte = primeiraPalavra && indice == 0
    
    val parteNormalizada = normalizarParaComparacao(parte)
    
    if (!primeiraParte && parteNormalizada in PALAVRAS_NAO_CONTABILIZADAS) {
        parte
      } else {
        capitalizarPrimeiraLetra(parte)
      }
  }.joinToString("-")
}

private fun criarChaveDeSigla(
  palavra: String
): String {
  return palavra.uppercase(LOCALE_PT_BR).replace(
    Regex("[^\\p{L}\\p{N}]"), ""
    )
}

private fun normalizarParaComparacao(
  palavra: String
): String {
  return palavra.lowercase(LOCALE_PT_BR).replace(
    Regex("^[^\\p{L}]+|[^\\p{L}]+$"), ""
    )
}

private fun capitalizarPrimeiraLetra(
  texto: String
): String {
  val indice = texto.indexOfFirst { it.isLetter() }
  
  if (indice == -1) {
    return texto
  }
  
  return buildString {
    append(texto.substring(0, indice))
    
    append(
      texto.substring(indice, indice + 1).uppercase(LOCALE_PT_BR)
    )
    
    append(texto.substring(indice + 1))
  }
}

