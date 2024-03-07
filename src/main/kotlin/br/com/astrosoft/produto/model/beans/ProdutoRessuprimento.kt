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
  var qtPedido: Int?,
  var qtQuantNF: Int?,
  var qtRecebido: Int?,
  var vlPedido: Double?,
  var vlQuantNF: Double?,
  var vlRecebido: Double?,
  var preco: Double?,
  var total: Double?,
  var marca: Int?,
  var selecionado: Int?,
  var posicao: Int?,
  var estoque: Int?,
  var codigoCorrecao: String?,
  var descricaoCorrecao: String?,
  var gradeCorrecao: String?
) {
  val statusStr = EMarcaNota.entries.firstOrNull { it.num == marca }?.descricao ?: ""
  val selecionadoOrdemCD get() = if (selecionado == EMarcaRessuprimento.CD.num) 0 else 1
  val selecionadoOrdemREC get() = if (selecionado == EMarcaRessuprimento.REC.num) 0 else 1
  val selecionadoOrdemENT get() = if (selecionado == EMarcaRessuprimento.ENT.num) 0 else 1

  fun salva() {
    saci.salvaProdutosRessuprimento(this)
  }

  fun findGrades(): List<PrdGrade> {
    return saci.findGrades(codigo ?: "")
  }
}
