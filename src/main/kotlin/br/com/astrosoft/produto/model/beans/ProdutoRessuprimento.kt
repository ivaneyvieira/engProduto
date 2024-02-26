package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoRessuprimento(
  var ordno: Long?,
  var codigo: String?,
  var grade: String?,
  var barcode: String?,
  var descricao: String?,
  var vendno: Int?,
  var fornecedor: String?,
  var typeno: Int?,
  var typeName: String?,
  var clno: String?,
  var clname: String?,
  var altura: Int?,
  var comprimento: Int?,
  var largura: Int?,
  var precoCheio: Double?,
  var localizacao: String?,
  var quantidade: Int?,
  var preco: Double?,
  var total: Double?,
  var marca: Int?,
  var selecionado: Boolean?,
  var estoque: Int?,
) {
  val statusStr = EMarcaNota.values().firstOrNull { it.num == marca }?.descricao ?: ""

  fun salva() {
    saci.salvaProdutosRessuprimento(this)
  }

  fun findGrades(): List<PrdGrade> {
    return saci.findGrades(codigo ?: "")
  }
}
