package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class Agenda {
  var loja: Int = 0
  var data: LocalDate? = null
  var hora: LocalTime? = null
  var empno: Int = 0
  var recebedor: String = ""
  var conhecimento: String = ""
  var emissaoConhecimento: LocalDate? = null
  var dataRecbedor: LocalDate? = null
  var horaRecebedor: LocalTime? = null
  var invno: String = ""
  var fornecedor: Int = 0
  var abreviacao: String = ""
  var cnpj: String? = null
  var emissao: LocalDate? = null
  var nf: String = ""
  var volume: String = ""
  var total: Double = 0.00
  var transp: Int = 0
  var nome: String = ""
  var pedido: Int = 0
  var frete: String? = null
  var coleta: LocalDate? = null

  val dataHoraRecebimento
    get() = "${dataRecbedor.format()} $horaRecebedor".trim()

  fun agendaUpdate() = AgendaUpdate(
    invno = invno.toIntOrNull() ?: 0,
    coleta = coleta,
    data = data,
    hora = hora,
    recebedor = empno,
    conhecimento = conhecimento,
    emissaoConhecimento = emissaoConhecimento,
    dataRecbedor = dataRecbedor,
    horaRecebedor = horaRecebedor,
  )

  companion object {
    fun listaAgenda(filtro: FiltroAgenda) = saci.listaAgenda(filtro)
  }
}

data class FiltroAgenda(
  val loja: Int,
  val pesquisa: String,
)