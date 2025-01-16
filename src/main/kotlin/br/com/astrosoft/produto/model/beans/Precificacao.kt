package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import kotlin.math.absoluteValue

class Precificacao {
  var prdno: String? = null
  var codigo: String? = null
  var descricao: String? = null
  var cpmf: Double? = null
  var vendno: Int? = null
  var fornecedor: String? = null
  var tributacao: String? = null
  var typeno: Int? = null
  var clno: Int? = null
  var mvap: Double? = null
  var icmsp: Double? = null
  var fcp: Double? = null
  var pcfabrica: Double? = null
  var ipi: Double? = null
  var embalagem: Double? = null
  var retido: Double? = null
  var creditoICMS: Double? = null
  var frete: Double? = null
  var custoContabil: Double? = null
  var icms: Double? = null
  var pis: Double? = null
  var ir: Double? = null
  var contrib: Double? = null
  var fixa: Double? = null
  var outras: Double? = null
  var lucroLiq: Double? = null
  var precoSug: Double? = null
  var precoRef: Double? = null
  var precoDif: Double? = null
  var ncm: String? = null
  var rotulo: String? = null
  var freteICMS: Double? = null
  var precoCusto: Double? = null
  var cfinanceiro: Double? = null
  var impostos: String? = null

  val impostoList = impostos?.split("\\") ?: emptyList()

  fun icmsEntradaMa(icms: String): Double? {
    val imposto = impostoList.firstOrNull { linha ->
      val parte = linha.split(" +".toRegex())
      parte.getOrNull(0) == "ICMS" &&
          parte.getOrNull(1) == "ENTRADA" &&
          parte.getOrNull(2) == icms &&
          parte.getOrNull(3) == "MVA"

    } ?: return null
    return imposto.split(" +".toRegex()).getOrNull(2)?.replace(',', '.')?.toDoubleOrNull()
  }

  val mvaMa00: Double?
    get() = mvaMa("0")

  val mvaMa04: Double?
    get() = mvaMa("4")

  val mvaMa07: Double?
    get() = mvaMa("7")

  val mvaMa12: Double?
    get() = mvaMa("12")

  fun mvaMa(icms: String): Double? {
    val imposto = impostoList.firstOrNull { linha ->
      val parte = linha.split(" +".toRegex())
      parte.getOrNull(0) == "ICMS" &&
          parte.getOrNull(1) == "ENTRADA" &&
          parte.getOrNull(2) == icms &&
          parte.getOrNull(3) == "MVA"

    } ?: return null
    return imposto.split(" +".toRegex()).getOrNull(4)?.replace(',', '.')?.toDoubleOrNull()
  }

  fun mvaMaOrig(): Double? {
    val mvaOri = impostoList.firstOrNull { linha ->
      val parte = linha.split(" +".toRegex())
      parte.getOrNull(0) == "MVA" &&
          parte.getOrNull(1) == "ORIGINAL"

    } ?: return null
    return mvaOri.split(" +".toRegex()).getOrNull(2)?.replace(',', '.')?.toDoubleOrNull()
  }

  fun ncmMa(): String? {
    val ncmMa = impostoList.firstOrNull { linha ->
      val parte = linha.split(" +".toRegex())
      parte.getOrNull(0) == "TIMON" &&
          parte.getOrNull(1) == "-" &&
          parte.getOrNull(2) == "MA" &&
          parte.getOrNull(3) == "NCM"

    } ?: return null
    return ncmMa.split(" +".toRegex()).getOrNull(4)
  }

  val ncmMa: String?
    get() = ncmMa()

  val mvaMaOrig: Double?
    get() = mvaMaOrig()

  val diferencaCusto
    get() = (custoContabil ?: 0.00) - (precoCusto ?: 0.00)
  val freteICMSCalc: Double?
    get() {
      val freteCalc = frete ?: return null
      val icmsCalc = icmsp ?: return null
      val calc = freteCalc * (icmsCalc / 100)
      return calc.absoluteValue
    }

  fun save() {
    saci.savePrecificacao(this)
  }

  companion object {
    fun findAll(filtro: FiltroPrecificacao) = saci.listaPrecificacao(filtro)
    fun updateItens(list: List<Precificacao>, bean: BeanForm) {
      saci.saveListPrecificacao(list, bean)
    }
  }
}

data class FiltroPrecificacao(
  val codigo: Int,
  val listVend: List<Int>,
  val tributacao: String,
  val typeno: String,
  val clno: Int,
  val marcaPonto: EMarcaPonto,
  val query: String,
)