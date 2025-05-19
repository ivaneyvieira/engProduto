package br.com.astrosoft.produto.model.beans

class NotaSaidaDevProduto {
  var loja: Int? = null
  var pdvno: Int? = null
  var xano: Int? = null
  var codigo: Int? = null
  var prdno: String? = null
  var grade: String? = null
  var descricao: String? = null
  var un: String? = null
  var cfop: String? = null
  var cst: String? = null
  var ncm: String? = null
  var quantidade: Int? = null
  var local: String? = null
  var barcodeStrList: String? = null
  var preco: Double? = null
  var desconto: Double? = null
  var frete: Double? = null
  var despesas: Double? = null
  var baseIcms: Double? = null
  var valorSubst: Double? = null
  var baseSubst: Double? = null
  var valorIcms: Double? = null
  var valorIpi: Double? = null
  var aliquotaIcms: Double? = null
  var aliquotaIpi: Double? = null
  var total: Double? = null

  val totalGeral: Double
    get() {
      return (total ?: 0.00) + (frete ?: 0.00) +
             (despesas ?: 0.00) + (valorIpi ?: 0.00) +
             (valorSubst ?: 0.00) - (desconto ?: 0.00)
    }
}