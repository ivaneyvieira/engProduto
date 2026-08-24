package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci

class DadosPrecificacao {

  var prdno: String? = null
  var descricao: String? = null
  var taxno: String? = null
  var precoFabrica02: Double? = null
  var precoFabrica03: Double? = null
  var precoFabrica04: Double? = null
  var precoFabrica05: Double? = null
  var precoFabrica08: Double? = null
  var precoFabrica10: Double? = null
  var percentualIPI02: Double? = null
  var percentualIPI03: Double? = null
  var percentualIPI04: Double? = null
  var percentualIPI05: Double? = null
  var percentualIPI08: Double? = null
  var percentualIPI10: Double? = null
  var creditoICMS02: Double? = null
  var creditoICMS03: Double? = null
  var creditoICMS04: Double? = null
  var creditoICMS05: Double? = null
  var creditoICMS08: Double? = null
  var creditoICMS10: Double? = null
  var embalagem02: Double? = null
  var embalagem03: Double? = null
  var embalagem04: Double? = null
  var embalagem05: Double? = null
  var embalagem08: Double? = null
  var embalagem10: Double? = null
  var custoContabil02: Double? = null
  var custoContabil03: Double? = null
  var custoContabil04: Double? = null
  var custoContabil05: Double? = null
  var custoContabil08: Double? = null
  var custoContabil10: Double? = null
  var creditoPisCofins02: Double? = null
  var creditoPisCofins03: Double? = null
  var creditoPisCofins04: Double? = null
  var creditoPisCofins05: Double? = null
  var creditoPisCofins08: Double? = null
  var creditoPisCofins10: Double? = null
  var frete02: Double? = null
  var frete03: Double? = null
  var frete04: Double? = null
  var frete05: Double? = null
  var frete08: Double? = null
  var frete10: Double? = null
  var retido02: Double? = null
  var retido03: Double? = null
  var retido04: Double? = null
  var retido05: Double? = null
  var retido08: Double? = null
  var retido10: Double? = null

  val codigo
    get() = prdno?.trim()?.toIntOrNull() ?: 0

  fun valores(campo: ECampoPrecificacao, loja: ELojaProcificcao): List<LojaValor> {
    return when (campo) {
      ECampoPrecificacao.PRECO -> valorPreco(loja)
      ECampoPrecificacao.IPI   -> valorIpi(loja)
      ECampoPrecificacao.ICMS  -> valorIcms(loja)
    }
  }

  private fun valorPreco(loja: ELojaProcificcao): List<LojaValor> {
    return when (loja) {
      ELojaProcificcao.TODAS -> valorPreco(ELojaProcificcao.ADM) + valorPreco(ELojaProcificcao.MF) +
                                valorPreco(ELojaProcificcao.PK) + valorPreco(ELojaProcificcao.MR) +
                                valorPreco(ELojaProcificcao.DS) + valorPreco(ELojaProcificcao.TM)

      ELojaProcificcao.ADM   -> listOf(LojaValor(ELojaProcificcao.ADM, precoFabrica10 ?: 0.00))
      ELojaProcificcao.MF    -> listOf(LojaValor(ELojaProcificcao.MF, precoFabrica04 ?: 0.00))
      ELojaProcificcao.PK    -> listOf(LojaValor(ELojaProcificcao.PK, precoFabrica05 ?: 0.00))
      ELojaProcificcao.MR    -> listOf(LojaValor(ELojaProcificcao.MR, precoFabrica03 ?: 0.00))
      ELojaProcificcao.DS    -> listOf(LojaValor(ELojaProcificcao.DS, precoFabrica02 ?: 0.00))
      ELojaProcificcao.TM    -> listOf(LojaValor(ELojaProcificcao.TM, precoFabrica08 ?: 0.00))
    }
  }

  private fun valorIpi(loja: ELojaProcificcao): List<LojaValor> {
    return when (loja) {
      ELojaProcificcao.TODAS -> valorIpi(ELojaProcificcao.ADM) + valorIpi(ELojaProcificcao.MF) +
                                valorIpi(ELojaProcificcao.PK) + valorIpi(ELojaProcificcao.MR) +
                                valorIpi(ELojaProcificcao.DS) + valorIpi(ELojaProcificcao.TM)

      ELojaProcificcao.ADM   -> listOf(LojaValor(ELojaProcificcao.ADM, percentualIPI10 ?: 0.00))
      ELojaProcificcao.MF    -> listOf(LojaValor(ELojaProcificcao.MF, percentualIPI04 ?: 0.00))
      ELojaProcificcao.PK    -> listOf(LojaValor(ELojaProcificcao.PK, percentualIPI05 ?: 0.00))
      ELojaProcificcao.MR    -> listOf(LojaValor(ELojaProcificcao.MR, percentualIPI03 ?: 0.00))
      ELojaProcificcao.DS    -> listOf(LojaValor(ELojaProcificcao.DS, percentualIPI02 ?: 0.00))
      ELojaProcificcao.TM    -> listOf(LojaValor(ELojaProcificcao.TM, percentualIPI08 ?: 0.00))
    }
  }

  private fun valorIcms(loja: ELojaProcificcao): List<LojaValor> {
    return when (loja) {
      ELojaProcificcao.TODAS -> valorIcms(ELojaProcificcao.ADM) + valorIcms(ELojaProcificcao.MF) +
                                valorIcms(ELojaProcificcao.PK) + valorIcms(ELojaProcificcao.MR) +
                                valorIcms(ELojaProcificcao.DS) + valorIcms(ELojaProcificcao.TM)

      ELojaProcificcao.ADM   -> listOf(LojaValor(ELojaProcificcao.ADM, creditoICMS10 ?: 0.00))
      ELojaProcificcao.MF    -> listOf(LojaValor(ELojaProcificcao.MF, creditoICMS04 ?: 0.00))
      ELojaProcificcao.PK    -> listOf(LojaValor(ELojaProcificcao.PK, creditoICMS05 ?: 0.00))
      ELojaProcificcao.MR    -> listOf(LojaValor(ELojaProcificcao.MR, creditoICMS03 ?: 0.00))
      ELojaProcificcao.DS    -> listOf(LojaValor(ELojaProcificcao.DS, creditoICMS02 ?: 0.00))
      ELojaProcificcao.TM    -> listOf(LojaValor(ELojaProcificcao.TM, creditoICMS08 ?: 0.00))
    }
  }

  companion object {
    fun findAll(filtro: FiltroDadosPrecificacao): List<DadosPrecificacao> {
      return saci.precificacaoDados(filtro)
    }
  }
}

data class FiltroDadosPrecificacao(val pesquisa: String)

enum class ELojaProcificcao(val sigla: String, val codigo: Int) {
  TODAS(sigla = "TODAS", codigo = 0),
  ADM(sigla = "ADM", codigo = 10),
  MF(sigla = "MF", codigo = 4),
  PK(sigla = "PK", codigo = 5),
  MR(sigla = "MR", codigo = 3),
  DS(sigla = "DS", codigo = 2),
  TM(sigla = "TM", codigo = 8)
}

enum class ECampoPrecificacao(val descricao: String) {
  PRECO("Preço"), IPI("IPI"), ICMS("ICMS")
}

private val PAT_NUM= "000000000.00"

enum class EOperacaoPrecificacao(val oper: String, val execute: (a: Double, b: Double) -> Boolean) {
  IGUAL(oper = "=", execute = { a, b ->
    a.format(PAT_NUM) == b.format(PAT_NUM)
  }),
  MAIOR(oper = ">", execute = { a, b ->
    a.format(PAT_NUM) > b.format(PAT_NUM)
  }),
  MENOR(oper = "<", execute = { a, b ->
    a.format(PAT_NUM) < b.format(PAT_NUM)
  }),
  DIFERENTE(oper = "≠", execute = { a, b ->
    a.format(PAT_NUM) != b.format(PAT_NUM)
  })
}

data class FiltroValoresPrecificacao(
  val lojaRef: ELojaProcificcao,
  val loja: ELojaProcificcao,
  val campo: ECampoPrecificacao,
  val operacao: EOperacaoPrecificacao,
)

data class LojaValor(val loja: ELojaProcificcao, val valor: Double)