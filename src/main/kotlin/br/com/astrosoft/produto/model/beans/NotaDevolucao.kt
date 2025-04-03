package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaDevolucao {
  var storeno: Int? = null
  var nota: String? = null
  var emissao: LocalDate? = null
  var valor: Double? = null
  var observacao: String? = null

  companion object {
    private val list = mutableListOf<NotaDevolucao>()

    fun update() {
      val listNova = saci.selectNotaDevolucao()
      list.clear()
      list.addAll(listNova)
    }

    fun findNotaDevolucao(loja: Int?, nfEntrada: String?): NotaDevolucao? {
      loja ?: return null
      nfEntrada ?: return null
      return list.firstOrNull { it.storeno == loja && (it.observacao?.contains(nfEntrada) ?: false) }
    }
  }
}