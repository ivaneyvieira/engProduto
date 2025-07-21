package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class FornecedorClass {
  var no: Int? = null
  var custno: Int? = null
  var descricao: String? = null
  var cnpjCpf: String? = null
  var classe: Int? = null
  var classificacao: String? = null

  companion object {
    fun findAll(filtro: FiltroFornecedor): List<FornecedorClass> {
      return saci.findFornecedorClass(filtro)
    }
  }
}

data class FiltroFornecedor(
  val pesquisa: String,
)