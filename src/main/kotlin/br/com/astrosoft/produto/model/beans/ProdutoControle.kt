package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import kotlin.math.roundToInt

class ProdutoControle(
  var loja: Int?,
  var lojaSigla: String?,
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var unidade: String?,
  var grade: String?,
  var tipo: Int?,
  var cl: Int?,
  var embalagem: Int?,
  var qtdEmbalagem: Double?,
  var estoque: Int?,
  var locNerus: String?,
  var locApp: String?,
  var codForn: Int?,
  var fornecedor: String?,
  var fornecedorAbrev: String?,
  var cnpjFornecedor: String?,
  var saldo: Int?,
  var valorEstoque: Double?,
  var saldoVarejo: Int?,
  var saldoAtacado: Int?,
  var dataInicial: LocalDate?,
  var dataUpdate: LocalDate?,
  var kardec: Int? = null,
  var dataConferencia: LocalDate? = null,
  var qtConfEditLoja: Int? = null,
  var qtConfEdit: Int? = null,
  var qtConferencia: Int? = null,
  var preco: Double? = null,
  var estoqueUser: Int? = null,
  var estoqueLogin: String? = null,
  var estoqueData: LocalDate? = null,
  var estoqueCD: Int? = null,
  var estoqueLoja: Int? = null,
  var barcode: String? = null,
  var ref: String? = null,
  var numeroAcerto: Int? = null,
  var processado: Boolean? = false,
  var estoqueConfCD: Int? = null,
  var estoqueConfLoja: Int? = null,
) {
  val kardecEmb: Double?
    get() {
      return when {
        descricao?.startsWith("SVS E-COLOR") == true -> {
          if (kardec == null) null else (kardec ?: 0) / 900.0
        }

        descricao?.startsWith("VRC COLOR") == true   -> {
          if (kardec == null) null else (kardec ?: 0) / 1000.0
        }

        else                                         -> {
          if (kardec == null) null else (kardec ?: 0) / ((embalagem ?: 0) * 1.00)
        }
      }
    }
  val qtConfCalc: Int
    get() = (qtConfEdit ?: 0) + (qtConfEditLoja ?: 0)

  val qtConfCalcEstoque: Int
    get() = (estoqueConfCD ?: 0) + (estoqueConfLoja ?: 0)

  val qtDifCalcEstoque: Int
    get() = qtConfCalcEstoque - (saldo ?: 0)

  fun isUpdated(): Boolean {
    return dataUpdate.toSaciDate() == LocalDate.now().toSaciDate()
  }

  val qtdDif: Double
    get() {
      val sistema = saldo?.toDouble() ?: 0.0
      val cd = kardec?.toDouble() ?: 0.0
      val diferenca = sistema - cd
      return if (descricao?.startsWith("SVS E") == true) {
        diferenca * 900.0 / 900.0
      } else {
        diferenca
      }
    }

  val qtdDifInt
    get() = qtdDif.roundToInt()

  val saldoBarraRef: String
    get() {
      return "${barcode ?: ""}   |   ${ref ?: ""}"
    }

  var marcadoConfProp: Boolean = false

  fun marcadoConf(userNo: Int, data: LocalDate): Boolean {
    this.marcadoConfProp = (estoqueUser == userNo) && (estoqueData.toSaciDate() == data.toSaciDate())
    return this.marcadoConfProp
  }

  val estoqueDif: Int?
    get() {
      if (estoqueCD == null && estoqueLoja == null) {
        return null
      }

      if (saldo == null) {
        return null
      }

      val estLoja = estoqueLoja ?: 0
      val estCD = estoqueCD ?: 0
      val estSaldo = saldo ?: 0

      return estLoja + estCD - estSaldo
    }

  val diferenca: Int
    get() {
      val estCD = kardec ?: 0
      val estSis = saldo ?: 0
      return estSis - estCD
    }

  val codigoStr
    get() = this.codigo?.toString() ?: ""

  companion object {
    fun findProdutoControle(filter: FiltroProdutoControle): List<ProdutoControle> {
      return saci.findProdutoControle(filter)
    }
  }
}

data class FiltroProdutoControle(
  val loja: Int = 0,
  val pesquisa: String,
  val codigo: Int,
  val grade: String,
  val caracter: ECaracter,
  val localizacao: String?,
  val fornecedor: String,
  val centroLucro: Int = 0,
  val pedido: Int = 0,
  val estoque: EEstoque = EEstoque.TODOS,
  val saldo: Int = 0,
  val inativo: EInativo,
  val uso: EUso = EUso.TODOS,
  val listaUser: List<String>,
  val letraDup: ELetraDup = ELetraDup.TODOS,
) {
  val prdno = if (codigo == 0) "" else codigo.toString().lpad(16, " ")
}