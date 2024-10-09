package br.com.astrosoft.devolucao.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class Agenda {
  var loja: Int = 0
  var data: LocalDate? = null
  var hora: String = ""
  var empno: Int = 0
  var recebedor: String = ""
  var conhecimento: String = ""
  var dataRecbedor: LocalDate? = null
  var horaRecebedor: String = ""
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

  companion object {
    fun listaAgenda(filtro: FiltroAgenda) = saci.listaAgenda(filtro)
  }
}

data class FiltroAgenda(
  val loja: Int,
  val pesquisa: String,
)