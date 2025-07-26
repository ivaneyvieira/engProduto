package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class Fornecedor(
  val vendno: Int,
  val nome: String,
  val cgc: String,
  val abrev: String,
) {
  companion object {
    fun findByVendno(vendno: Int): Fornecedor? {
      return saci.findFornecedorByNo(vendno)
    }
  }
}