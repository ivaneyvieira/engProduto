package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

data class MesAno(val mes: Int, val ano: Int) {
  val mesAnoFormat: String
    get() = "${mesAnoFormat(mes)}/$ano"
  val nomeMes: String
    get() = mesAnoFormat(mes)
  val firstDay: LocalDate
    get() = LocalDate.of(ano, mes, 1)
  val lastDay: LocalDate
    get() = firstDay.plusMonths(1).minusDays(1)

  private val meses = listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

  private fun mesAnoFormat(mes: Int): String {
    return meses.getOrNull(mes - 1) ?: "???"
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

