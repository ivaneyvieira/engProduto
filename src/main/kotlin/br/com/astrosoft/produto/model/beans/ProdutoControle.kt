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
  var barcode: String? = null,
  var ref: String? = null,
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

  val qtConfCalcEstoque: Int
    get() = (estoqueConfCD ?: 0) + (estoqueConfLoja ?: 0)

  val qtDifCalcEstoque: Int
    get() = qtConfCalcEstoque - (saldo ?: 0)

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

  var marcadoConfProp: Boolean = false

  fun marcadoConf(userNo: Int, data: LocalDate): Boolean {
    this.marcadoConfProp = (estoqueUser == userNo) && (estoqueData.toSaciDate() == data.toSaciDate())
    return this.marcadoConfProp
  }

  val diferenca: Int
    get() {
      val estCD = kardec ?: 0
      val estSis = saldo ?: 0
      return estSis - estCD
    }

  val codigoStr
    get() = this.codigo?.toString() ?: ""

  fun findKardec(dataInicial: LocalDate?): List<ControleKardec> {
    return saci.findProdutoKardec(
      loja = loja ?: return emptyList(),
      prdno = prdno ?: return emptyList(),
      grade = grade ?: return emptyList(),
      dataInicial = dataInicial
    )
  }

  companion object {
    fun findProdutoControle(filter: FiltroProdutoControle): List<ProdutoControle> {
      return saci.findProdutoControle(filter)
    }
  }
}

data class FiltroProdutoControle(
  val loja: Int,
  val pesquisa: String,
  val codigo: Int,
  val grade: String,
  val caracter: ECaracter,
  val localizacao: String?,
  val fornecedor: String,
  val centroLucro: Int,
  val estoque: EEstoque,
  val saldo: Int,
  val inativo: EInativo,
  val listaUser: List<String>,
  val letraDup: ELetraDup,
  val cl: Int,
) {
  val prdno = if (codigo == 0) "" else codigo.toString().lpad(16, " ")
}