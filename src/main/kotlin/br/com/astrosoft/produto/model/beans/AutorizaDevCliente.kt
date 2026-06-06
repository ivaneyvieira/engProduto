package br.com.astrosoft.produto.model.beans

import java.time.LocalDate
import java.time.LocalTime

data class AutorizaDevCliente(
  val invno: Int,
  val prdno: String,
  val grade: String,
  val userEntrega: Int,
  val dataEntrega: LocalDate?,
  val horaEntrega: LocalTime?,
  val userRecebimento: Int,
  val dataRecebimento: LocalDate?,
  val horaRecebimento: LocalTime?,
)
