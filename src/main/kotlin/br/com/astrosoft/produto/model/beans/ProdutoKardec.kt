package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

data class ProdutoKardec(
  val loja: Int,
  val prdno: String,
  val grade: String,
  val data: LocalDate?,
  val doc: String,
  val tipo: String,
  val vencimento: LocalDate? = null,
  val qtde: Int,
  val saldo: Int = 0,
){
  val codigo: Int
    get() = prdno.trim().toIntOrNull() ?: 0
}
