package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.toDate
import br.com.astrosoft.produto.nfeXml.ItensNotaReport
import java.time.LocalDate

class ProdutosNotaSaida(
  val loja: Int,
  val pdv: Int,
  val transacao: Int,
  val codigo: Int,
  val refFor: String,
  val refName: String,
  val descricao: String,
  val grade: String,
  val qtde: Int,
  val barcode: String,
  val un: String,
  val st: String,
  val valorUnitario: Double,
  val valorTotal: Double,
  val invno: Int,
  val vendno: Int,
  val vendnoNota: Int?,
  val rotulo: String,
  val quantInv: Int,
  val notaInv: String,
  val dateInv: LocalDate?,
  val valorUnitInv: Double,
  val valorTotalInv: Double,
  val ipi: Double,
  val baseSt: Double,
  val vst: Double,
  val valorTotalIpi: Double,
  val chaveUlt: String?,
  val cst: String,
  val cfop: String,
  val ncm: String,
  val baseICMS: Double,
  val valorICMS: Double,
  val baseIPI: Double,
  val valorIPI: Double,
  val icmsAliq: Double,
  val ipiAliq: Double,
  val sefazOk: String,
  val chaveSefaz: String?,
  val cfopProduto: String?
) {
  private val produtosNDD = mutableListOf<ItensNotaReport>()

  val codigoStr
    get() = codigo.toString()

  fun setProdutoNdd(produtosNDD: List<ItensNotaReport>) {
    this.produtosNDD.clear()
    this.produtosNDD.addAll(produtosNDD)
  }

  val descricaoFornecedor
    get() = produtosNDD.firstOrNull { it.codigo == refFor }?.descricao ?: refName

  var item: Int = 0
  val dateInvDate
    get() = dateInv?.toDate()
  val dateInvStr
    get() = dateInv.format()
  var nota: NotaSaida? = null

  val valorMVA
    get() = if ((baseICMS + valorIPI) == 0.00) 0.00 else baseSt / (baseICMS + valorIPI)
  val valorST
    get() = vst
  val valorTotalGeral
    get() = valorTotal + valorST + valorIPI
  val invnoObs: String
    get() {
      val obs = if (sefazOk == "N") "*" else ""
      return "$obs$invno"
    }
}