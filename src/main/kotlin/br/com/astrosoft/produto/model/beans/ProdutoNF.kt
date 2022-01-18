package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoNF(
  val loja: Int,
  val pdvno: Int,
  val xano: Long,
  val nota: String,
  val codigo: String,
  val grade: String,
  val barcode: String,
  val descricao: String,
  val vendno: Int,
  val fornecedor: String,
  val typeno: Int,
  val typeName: String,
  val clno: String,
  val clname: String,
  val altura: Int,
  val comprimento: Int,
  val largura: Int,
  val precoCheio: Double,
  val ncm: String,
  val localizacao: String?,
  val quantidade: Int,
  val preco: Double,
  val total: Double,
  var gradeAlternativa: String?,
  var marca: Int,
  var usuarioExp: String,
  var usuarioCD: String,
  val tipoNota: Int,
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
