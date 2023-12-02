package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class Nota(
  var numero: String?,
  var serie: String?,
  var data: LocalDate?,
  var cliente: Int?,
  var nomeCliente: String?,
)