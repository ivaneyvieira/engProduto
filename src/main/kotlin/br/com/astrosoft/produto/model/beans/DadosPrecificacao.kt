package br.com.astrosoft.produto.model.beans

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

  companion object {
    fun findAll(filtro: FiltroDadosPrecificacao): List<DadosPrecificacao> {
      return saci.precificacaoDados(filtro)
    }
  }
}

data class FiltroDadosPrecificacao(val pesquisa: String)