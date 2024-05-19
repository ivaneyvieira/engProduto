package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import java.text.DecimalFormat
import java.time.LocalDate

data class MesAno(val mes: Int, val ano: Int) {
  val mesAnoFormat: String
    get() = "${mesFormat(mes)}/${anoFormat(ano)}"
  val firstDay: LocalDate
    get() = LocalDate.of(ano, mes, 1)
  val lastDay: LocalDate
    get() = firstDay.plusMonths(1).minusDays(1)

  private fun mesFormat(mes: Int): String {
    val formatInteger = DecimalFormat("00")
    return formatInteger.format(mes)
  }

  private fun anoFormat(ano: Int): String {
    val anoSimles = ano % 100
    val formatInteger = DecimalFormat("00")
    return formatInteger.format(anoSimles)
  }

  fun ym() = ano * 100 + mes

  companion object {
    fun values(): List<MesAno> {
      val mesAtual = LocalDate.now().withDayOfMonth(15)
      val listNum = (0..12 * 10).toList()
      return listNum.map {
        val mes = mesAtual.minusMonths(it.toLong())
        MesAno(mes.monthValue, mes.year)
      }
    }

    fun valuesFuture() : List<MesAno> {
      val mesAtual = LocalDate.now().withDayOfMonth(15)
      val listNum = (0..12 * 10).toList()
      return listNum.map {
        val mes = mesAtual.plusMonths(it.toLong())
        MesAno(mes.monthValue, mes.year)
      }
    }

    fun now() = MesAno(LocalDate.now().monthValue, LocalDate.now().year)
  }
}

