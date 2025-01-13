package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class QtdVencimento {
  var storeno: Int? = null
  var prdno: String? = null
  var grade: String? = null
  var vendas: Int? = null
  var dataVenda: LocalDate? = null
  var qtty01: Int? = null
  var venc01: String? = null
  var qtty02: Int? = null
  var venc02: String? = null
  var qtty03: Int? = null
  var venc03: String? = null
  var qtty04: Int? = null
  var venc04: String? = null

  val qttyInv: Int?
    get() = (qtty01 ?: 0).plus(qtty02 ?: 0).plus(qtty03 ?: 0).plus(qtty04 ?: 0)

  val qttyDif01: Int?
    get() {
      val vendas = this.vendas ?: return this.qtty01
      val dif = (this.qtty01 ?: 0) - vendas
      return if (dif < 0) 0 else dif
    }

  val qttyDif02: Int?
    get() {
      val vendas = this.vendas ?: return this.qtty02
      if ((qttyDif01 ?: 0) > 0) return this.qtty02
      val dif = (this.qtty01 ?: 0) + (this.qtty02 ?: 0) - vendas
      return if (dif < 0) 0 else dif
    }

  val qttyDif03: Int?
    get() {
      val vendas = this.vendas ?: return this.qtty03
      if ((qttyDif01 ?: 0) > 0 || (qttyDif02 ?: 0) > 0) return this.qtty03
      val dif = (this.qtty01 ?: 0) + (this.qtty02 ?: 0) + (this.qtty03 ?: 0) - vendas
      return if (dif < 0) 0 else dif
    }

  val qttyDif04: Int?
    get() {
      val vendas = this.vendas ?: return this.qtty03
      if ((qttyDif01 ?: 0) > 0 || (qttyDif02 ?: 0) > 0 || (qttyDif03 ?: 0) > 0) return this.qtty04
      val dif = (this.qtty01 ?: 0) + (this.qtty02 ?: 0) + (this.qtty03 ?: 0) + (this.qtty04 ?: 0) - vendas
      return dif
    }
}