package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class DadosDevProduto(
  var ni: Int? = null,
  var loja: Int? = null,
  var nomeLoja: String?,
  var nfdno: String? = null,
  var nfdse: String? = null,
  var dataDevolucao: LocalDate? = null,
  var valorDev: Double? = null,
  var obs: String? = null,
  var nfVenda: String? = null,
  var obsNotaVenda: String?,
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
  var empno: Int?,
  var vendedor: String? = null,
  var custnoVend: Int?,
  var nomeVend: String?,
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
  val codigoFormat: String
    get() = prdno?.trim()?.padStart(6, '0') ?: ""

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
    if (prdno != other.prdno) return false
    if (grade != other.grade) return false
    if (produtoTrocaItem != other.produtoTrocaItem) return false

    return true
  }

  override fun hashCode(): Int {
    var result = ni ?: 0
    result = 31 * result + (prdno?.hashCode() ?: 0)
    result = 31 * result + (grade?.hashCode() ?: 0)
    result = 31 * result + (produtoTrocaItem?.hashCode() ?: 0)
    return result
  }

  override fun toString(): String {
    return "DadosDevProduto(ni=$ni, loja=$loja, nfdno=$nfdno, nfdse=$nfdse, dataDevolucao=$dataDevolucao, valorDev=$valorDev, obs=$obs, nfVenda=$nfVenda, obsTipo=$obsTipo, dataVenda=$dataVenda, codCliente=$codCliente, nomeCliente=$nomeCliente, prdno=$prdno, grade=$grade, descricao=$descricao, unidade=$unidade, quantidadeDev=$quantidadeDev, valorUnitario=$valorUnitario, nfno=$nfno, nfse=$nfse, pdvno=$pdvno, xano=$xano, nfTipo=$nfTipo, vendedor=$vendedor, notaEntrega=$notaEntrega, userSolicitacao=$userSolicitacao, loginSolicitacao=$loginSolicitacao, nomeSolicitacao=$nomeSolicitacao, userTroca=$userTroca, loginTroca=$loginTroca, nomeTroca=$nomeTroca, produtoTroca=$produtoTroca, tipoDev=$tipoDev, nfEntRet=$nfEntRet, produtoTrocaItem=$produtoTrocaItem, quantidadeTipo=$quantidadeTipo, dev=$dev)"
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
  return this.groupBy { "${it.ni} ${it.prdno} ${it.grade}" }.flatMap { entry ->
    val lista = entry.value
    val listaDev = lista.filter { it.dev == true }.ifEmpty {
      return@flatMap lista
    }

    val item = lista.firstOrNull() ?: return@flatMap emptyList()
    val quantNF = lista.firstOrNull()?.quantidadeDev ?: 0
    val quantDev = listaDev.sumOf { it.quantidadeTipo ?: 0 }
    val quantDif = quantNF - quantDev

    val listaDif = if (quantDif > 0) {
      val produtoTrocaItem = if (item.produtoTrocaItem == "C") "S" else if (item.produtoTrocaItem == "S") "C" else null
      listOf(
        item.copy().apply {
          this.dev = false
          this.produtoTrocaItem = produtoTrocaItem
          this.quantidadeTipo = quantDif
        }
      )
    } else {
      emptyList()
    }
    listaDev + listaDif
  }
}