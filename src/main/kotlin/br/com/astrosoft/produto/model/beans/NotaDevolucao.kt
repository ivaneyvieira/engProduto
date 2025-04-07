package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalDateTime

class NotaDevolucao {
  var storeno: Int? = null
  var nota: String? = null
  var emissao: LocalDate? = null
  var valor: Double? = null
  var observacao: String? = null

  fun tipoDevolucaoFound(): List<ETipoDevolucao> {
    val obs = observacao ?: return emptyList()
    return ETipoDevolucao.findByObs(obs)
  }

  companion object {
    private var timeUpdate = LocalDateTime.now()
    private val list = mutableListOf<NotaDevolucao>()

    fun update() {
      val time = LocalDateTime.now()
      if (timeUpdate.isBefore(time)) {
        val listNova = saci.selectNotaDevolucao()
        list.clear()
        list.addAll(listNova)
        timeUpdate = time.plusMinutes(5)
      }
    }

    fun findNotaDevolucao(loja: Int?, nfEntrada: String?, tipoDevolucao: ETipoDevolucao?): NotaDevolucao? {
      if (list.isEmpty()) {
        update()
      }
      loja ?: return null
      nfEntrada ?: return null
      return list.firstOrNull {
        (it.storeno == loja) &&
        (it.observacao?.contains(nfEntrada) ?: false) &&
        (tipoDevolucao in it.tipoDevolucaoFound())
      }
    }
  }
}