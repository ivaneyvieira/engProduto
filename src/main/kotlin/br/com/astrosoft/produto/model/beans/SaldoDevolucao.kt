package br.com.astrosoft.produto.model.beans

class SaldoDevolucao(
  var invno: Int,
  var custnoDev: Int,
  var custnoMuda: Int,
  var tipo: String,
  var saldo: Double,
  var notaDev: EntradaDevCli? = null,
)