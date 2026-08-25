package br.com.astrosoft.produto.model.beans

data class BeanForm(
  var loja: Int = 10,
  var mvap: Double? = null,
  var creditoICMS: Double? = null,
  var pcfabrica: Double? = null,
  var ipi: Double? = null,
  var embalagem: Double? = null,
  var retido: Double? = null,
  var icmsp: Double? = null,
  var frete: Double? = null,
  var freteICMS: Double? = null,
  var pisCofins: Double? = null,
  var icms: Double? = null,
  var fcp: Double? = null,
  var pis: Double? = null,
  var ir: Double? = null,
  var contrib: Double? = null,
  var cpmf: Double? = null,
  var fixa: Double? = null,
  var outras: Double? = null,
)