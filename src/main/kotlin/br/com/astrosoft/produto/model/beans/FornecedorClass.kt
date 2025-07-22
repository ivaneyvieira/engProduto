package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class FornecedorClass {
  var no: Int? = null
  var custno: Int? = null
  var descricao: String? = null
  var cnpjCpf: String? = null
  var classe: Int? = null
  var classificacao: String? = null
  var termDev: String? = null
  var countArq: Int? = null
  var obs: String? = null

  fun save() {
    saci.fornecedorClassSave(this)
  }

  fun arquivos(): List<FornecedorArquivo> {
    return FornecedorArquivo.find(this.no ?: 0)
  }

  fun salvaArquivo(filename: String, file: ByteArray) {
    val vendno = this.no ?: return
    FornecedorArquivo.save(vendno, filename, file)
  }

  companion object {
    fun findAll(filtro: FiltroFornecedor): List<FornecedorClass> {
      return saci.findFornecedorClass(filtro)
    }
  }
}

data class FiltroFornecedor(
  val pesquisa: String,
)