package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

data class AgendaUpdate(
  val invno: Int,
  var coleta: LocalDate?,
  var data: LocalDate?,
  var hora: LocalTime?,
  var recebedor: String?,
  var conhecimento: String?,
  var emissaoConhecimento: LocalDate? = null,
  val dataRecbedor: LocalDate?,
  val horaRecebedor: LocalTime?,
) {
  fun save() {
    saci.updateAgenda(this)
  }
}