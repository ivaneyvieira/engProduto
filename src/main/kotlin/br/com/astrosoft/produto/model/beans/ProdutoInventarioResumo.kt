package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

data class ProdutoInventarioResumo(
  val prdno: String,
  val codigo: String,
  val grade: String,
  val dataEntrada: LocalDate?,
  val estoqueTotal: Int?,
  val estoqueDS: Int?,
  val estoqueMR: Int?,
  val estoqueMF: Int?,
  val estoquePK: Int?,
  val estoqueTM: Int?,
  val saldo: Int?,
  val vencimentoStr: String?,
  val saldoDS: Int?,
  val saldoMR: Int?,
  val saldoMF: Int?,
  val saldoPK: Int?,
  val saldoTM: Int?,
  val saidaDS: Int?,
  val saidaMR: Int?,
  val saidaMF: Int?,
  val saidaPK: Int?,
  val saidaTM: Int?,
  )