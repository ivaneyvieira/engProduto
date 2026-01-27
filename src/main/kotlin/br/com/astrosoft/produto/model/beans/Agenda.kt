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

  val dias: Int?
    get() {
      val emiss = emissao ?: return null
      return data?.let { -LocalDate.now().until(emiss).days }
    }

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

  val tipoAgenda: ETipoAgenda
    get() {
      return when {
        hora == null                -> ETipoAgenda.PENDENTE
        hora.format() == "06:00:00" -> ETipoAgenda.PREVISTO
        else                        -> ETipoAgenda.CONFIRMADO
      }
    }

  companion object {
    fun listaAgenda(filtro: FiltroAgenda) = saci.listaAgenda(filtro)
  }
}

data class FiltroAgenda(
  val loja: Int,
  val pesquisa: String,
  val tipoAgenda: ETipoAgenda,
)

enum class ETipoAgenda(val descricao: String) {
  CONFIRMADO("Confirmado"),
  PREVISTO("Previsto"),
  PENDENTE("Pendente"),
  TODOS("Todos")
}