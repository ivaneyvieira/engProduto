package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class DadosDevProduto(
  var ni: Int? = null,
  var loja: Int? = null,
  var nfdno: String? = null,
  var nfdse: String? = null,
  var dataDevolucao: LocalDate? = null,
  var valorDev: Double? = null,
  var obs: String? = null,
  var nfVenda: String? = null,
  var obsTipo: String? = null,
  var dataVenda: LocalDate? = null,
  var codCliente: Int? = null,
  var nomeCliente: String? = null,
  var prdno: String? = null,
  var grade: String? = null,
  var descricao: String? = null,
  var unidade: String? = null,
  var quantidadeDev: Int? = null,
  var valorUnitario: Double? = null,
  var nfno: Int? = null,
  var nfse: String? = null,
  var pdvno: Int? = null,
  var xano: Int? = null,
  var nfTipo: Int? = null,
  var vendedor: String? = null,
  var notaEntrega: String? = null,
  var userSolicitacao: Int? = null,
  var loginSolicitacao: String? = null,
  var nomeSolicitacao: String? = null,
  var userTroca: Int? = null,
  var loginTroca: String? = null,
  var nomeTroca: String? = null,
  var produtoTroca: String? = null,
  var tipoDev: String? = null,
  var nfEntRet: Int? = null,
  var produtoTrocaItem: String? = null,
  var quantidadeTipo: Int? = null,
  var dev: Boolean? = false
) {


  fun update() {
    saci.updateDadosDevProduto(this)
  }

  fun deleteDados() {
    saci.deleteDadosDevProduto(this)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as DadosDevProduto

    if (ni != other.ni) return false
    if (pdvno != other.pdvno) return false
    if (grade != other.grade) return false
    if (produtoTroca != other.produtoTroca) return false

    return true
  }

  override fun hashCode(): Int {
    var result = ni ?: 0
    result = 31 * result + (pdvno ?: 0)
    result = 31 * result + (grade?.hashCode() ?: 0)
    result = 31 * result + (produtoTroca?.hashCode() ?: 0)
    return result
  }

  val codigo: Int?
    get() = prdno?.trim()?.toIntOrNull()

  val valorTotal: Double
    get() = (valorUnitario ?: 0.0) * (quantidadeDev ?: 0)

  var produtoTrocaItemEnum: EProdutoTroca?
    get() = EProdutoTroca.entries.firstOrNull { it.codigo == produtoTrocaItem }
    set(value) {
      produtoTrocaItem = value?.codigo
    }

  var temProduto: Boolean?
    get() {
      val prdTroca = produtoTrocaItemEnum ?: return null
      return prdTroca == EProdutoTroca.Com
    }
    set(value) {
      produtoTrocaItem = if (value == null) {
        null
      } else
        if (value) {
          EProdutoTroca.Com.codigo
        } else {
          EProdutoTroca.Sem.codigo
        }
    }
}

fun List<DadosDevProduto>.expande(): List<DadosDevProduto> {
  //TODO
  return this
}