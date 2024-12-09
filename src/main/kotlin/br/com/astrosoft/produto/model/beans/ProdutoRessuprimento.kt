package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import org.apache.commons.math3.dfp.DfpField
import java.math.RoundingMode
import java.time.LocalDate

class ProdutoRessuprimento(
  var ordno: Int?,
  var prdno: String?,
  var codigo: String?,
  var grade: String?,
  var barcodeListStr: String?,
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
  var qttyOriginal: Int?,
  var qtQuantNF: Int?,
  var qtRecebido: Int?,
  var qtEntregue: Int?,
  var qtAvaria: Int?,
  var qtVencido: Int?,
  var vlPedido: Double?,
  var vlQuantNF: Double?,
  //var vlRecebido: Double?,
  var preco: Double?,
  var total: Double?,
  var marca: Int?,
  var selecionado: Int?,
  var posicao: Int?,
  var estoque: Int?,
  var codigoCorrecao: String?,
  var descricaoCorrecao: String?,
  var gradeCorrecao: String?,
  var numeroNota: String?,
  var dataNota: LocalDate?,
  var origemSaci: String?,
  var origemApp: String?,
  var validade: Int?,
  var vencimentoStrList: String?,
) {
  val vencimento
    get() = vencimentoStrList?.split(",")?.lastOrNull()?.toIntOrNull()

  val vencimentoStr = vencimentoToStr(vencimento)

  fun pedidoNovo(): PedidoNovo? {
    return saci.pedidoNovo(ordno ?: 0)
  }

  fun separaPedido(ordnoNovo: Int): PedidoNovo? {
    return saci.separaPedido(this, ordnoNovo)
  }

  private fun vencimentoToStr(vencimentoPar: Int?): String {
    val venc = vencimentoPar ?: 0
    val vencimentoStr = venc.toString()
    if (vencimentoStr.length != 6) {
      return ""
    } else {
      val mes = vencimentoStr.substring(4, 6)
      val ano = vencimentoStr.substring(2, 4)
      return "$mes/$ano"
    }
  }

  var estoqueLoja: Boolean? = false

  val qttyMax
    get() = if ((qttyOriginal ?: 0) < 5) 5 else ((this.qttyOriginal ?: 0) * 1.30).toBigDecimal()
      .setScale(0, RoundingMode.CEILING).toInt()

  val qttyMin
    get() = 1

  val statusStr = EMarcaNota.entries.firstOrNull { it.num == marca }?.descricao ?: ""
  val selecionadoOrdemCD get() = if (selecionado == EMarcaRessuprimento.CD.num) 0 else 1
  val selecionadoOrdemREC get() = if (selecionado == EMarcaRessuprimento.REC.num) 0 else 1
  val selecionadoOrdemENT get() = if (selecionado == EMarcaRessuprimento.ENT.num) 0 else 1

  val barcodeList: List<String>
    get() {
      val list = barcodeListStr?.split(",") ?: emptyList()
      return list.distinct().filter {
        it.isNotBlank()
      }
    }

  val barcodes get() = barcodeList.joinToString("\n")

  fun salva() {
    saci.salvaProdutosRessuprimento(this)
  }

  fun findGrades(): List<PrdGrade> {
    return saci.findGrades(codigo ?: "")
  }

  fun removerProduto() {
    saci.removerProduto(this)
  }

  fun salvaQuantidade() {
    saci.salvaQuantidadeProduto(this)
  }
}
