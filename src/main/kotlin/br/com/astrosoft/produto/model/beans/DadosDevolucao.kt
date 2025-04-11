package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class DadosDevolucao {
  var invno: Int? = null
  var numero: Int? = null
  var tipoDevolucao: Int? = null

  val tipoDevolucaoEnun
    get() = tipoDevolucao?.let {
      ETipoDevolucao.findByNum(it)
    }

  companion object {
    fun findNota(numero: Int): List<DadosDevolucao> {
      return saci.findDadosNotaDevolucao(numero)
    }
  }
}