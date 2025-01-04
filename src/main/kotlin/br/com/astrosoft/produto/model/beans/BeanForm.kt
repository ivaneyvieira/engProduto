package br.com.astrosoft.produto.model.beans

import java.math.BigDecimal

data class BeanForm(
  var mvap: BigDecimal? = null,
  var creditoICMS: BigDecimal? = null,
  var pcfabrica: BigDecimal? = null,
  var ipi: BigDecimal? = null,
  var embalagem: BigDecimal? = null,
  var retido: BigDecimal? = null,
  var icmsp: BigDecimal? = null,
  var frete: BigDecimal? = null,
  var freteICMS: BigDecimal? = null,
  var icms: BigDecimal? = null,
  var fcp: BigDecimal? = null,
  var pis: BigDecimal? = null,
  var ir: BigDecimal? = null,
  var contrib: BigDecimal? = null,
  var cpmf: BigDecimal? = null,
  var fixa: BigDecimal? = null,
  var outras: BigDecimal? = null,
)