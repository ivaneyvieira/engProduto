package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class PrdCodigo(val nfe: String, val prdno: String, val grade: String) {
  companion object {
    fun findPrdNfe(numero: String) = saci.findPrdNfe(numero)
  }
}