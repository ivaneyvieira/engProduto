package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

data class MovAtacado(
  val loja: Int,
  val codigo: Int,
  val descricao: String,
  val grade: String,
  val data: LocalDate?,
  val pedido: String,
  val transacao: String,
  val tipo: String,
  val qtEntrada: Int,
  val qtSaida: Int,
  val qtVarejo: Int,
  val qtTotal: Int,
) {
  companion object {
    fun findMovAtacado(filtro: MovManualFilter): List<MovAtacado> {
      return MovManual.findMovManual(filtro).toMovAtacado()
    }
  }
}

data class ChaveMovAtacado(val loja: Int, val codigo: Int, val grade: String)

fun List<MovManual>.toMovAtacado(): List<MovAtacado> {
  val group = this.groupBy {
    ChaveMovAtacado(it.loja ?: 0, it.codigoProduto ?: 0, it.grade ?: "")
  }
  return group.map { entry ->
    val (loja, codigo, grade) = entry.key
    val movs = entry.value
    val qtEntrada = movs.filter { (it.qtty ?: 0) >= 0 }.sumOf { it.estAtacado ?: 0 }
    val qtSaida = movs.filter { (it.qtty ?: 0) < 0 }.sumOf { it.estAtacado ?: 0 }
    val qtVarejo = movs.sumOf { it.estVarejo ?: 0 }
    val qtTotal = qtEntrada + qtSaida + qtVarejo
    MovAtacado(
      loja = loja,
      codigo = codigo,
      descricao = movs.first().nomeProduto ?: "",
      grade = grade,
      data = movs.mapNotNull { it.data }.maxOrNull(),
      pedido = movs.mapNotNull { it.pedido }.distinct().sorted().joinToString { it },
      transacao = movs.mapNotNull { it.transacao?.toString() }.distinct().sorted().joinToString { it },
      tipo = movs.mapNotNull { it.tipo }.distinct().sorted().joinToString { it },
      qtEntrada = qtEntrada,
      qtSaida = qtSaida,
      qtVarejo = qtVarejo,
      qtTotal = qtTotal
    )
  }
}