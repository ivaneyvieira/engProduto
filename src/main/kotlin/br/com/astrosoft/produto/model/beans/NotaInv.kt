package br.com.astrosoft.produto.model.beans

class NotaInv {
  var ni: Int? = null
  var loja: Int? = null
  var nfno: String? = null
  var nfse: String? = null

  val numeroNF: String
    get() {
      if (nfno == null || nfse == null) return ""
      return "$nfno/$nfse"
    }
}