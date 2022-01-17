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
  val localizacao: String?,
  val quantidade: Int,
  val preco: Double,
  val total: Double,
  var gradeAlternativa: String?,
  var marca: Int,
  var usuarioCD: String,
  val estoque: Int,
                        ) {
  private fun splitCD(index: Int) = usuarioCD.split("_").getOrNull(index) ?: ""

  val usuarioNameCD
    get() = splitCD(0)
  val dataCD
    get() = splitCD(1)
  val horaCD
    get() = splitCD(2)

  val statusStr = EMarcaNota.values().firstOrNull { it.num == marca }?.descricao ?: ""

  fun salva() {
    saci.salvaProdutosPedidoVenda(this)
  }

  fun expira() = saci.statusPedido(this, EStatusPedido.Expirado)

  fun orcamento() = saci.statusPedido(this, EStatusPedido.Orcado)

  fun findGrades(): List<PrdGrade> {
    return saci.findGrades(codigo)
  }
}
