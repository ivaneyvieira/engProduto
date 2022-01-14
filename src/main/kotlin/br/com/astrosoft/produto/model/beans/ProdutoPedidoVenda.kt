package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoPedidoVenda(
  val loja: Int,
  val ordno: Long,
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
  var usuarioCD: String,
  val estoque: Int,
                        ) {
  val usuarioNameCD
    get() = usuarioCD.split("_").getOrNull(0)

  val statusStr = EMarcaNota.values().firstOrNull { it.num == marca }?.descricao ?: ""

  fun salva() {
    saci.salvaProdutosPedidoVenda(this)
  }

  fun findGrades(): List<PrdGrade> {
    return saci.findGrades(codigo)
  }
}
