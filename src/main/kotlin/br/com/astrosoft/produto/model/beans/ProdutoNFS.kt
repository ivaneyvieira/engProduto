package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoNFS(
  var loja: Int,
  var pdvno: Int,
  var xano: Long,
  var nota: String,
  var codigo: String,
  var grade: String,
  var barcode: String,
  var descricao: String,
  var vendno: Int,
  var fornecedor: String,
  var typeno: Int,
  var typeName: String,
  var clno: String,
  var clname: String,
  var altura: Int,
  var comprimento: Int,
  var largura: Int,
  var precoCheio: Double,
  var ncm: String,
  var local: String,
  var quantidade: Int,
  var preco: Double,
  var total: Double,
  var gradeAlternativa: String?,
  var marca: Int,
  var usuarioExp: String,
  var usuarioCD: String,
  var tipoNota: Int,
) {
  private fun splitExp(index: Int) = usuarioExp.split("-").getOrNull(index) ?: ""

  val usuarioNameExp
    get() = splitExp(0)
  val dataExp
    get() = splitExp(1)
  val horaExp
    get() = splitExp(2)

  private fun splitCD(index: Int) = usuarioCD.split("-").getOrNull(index) ?: ""

  val usuarioNameCD
    get() = splitCD(0)
  val dataCD
    get() = splitCD(1)
  val horaCD
    get() = splitCD(2)

  val statusStr = EMarcaNota.values().firstOrNull { it.num == marca }?.descricao ?: ""

  fun salva() {
    saci.salvaProdutosNFS(this)
  }

  fun findGrades(): List<PrdGrade> {
    return saci.findGrades(codigo)
  }
}
