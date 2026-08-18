package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

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
  var quantidadeCom: Int? = null,
  var quantidadeSem: Int? = null,
  var localizacao: String? = null,
  var userEntregaNo: Int? = null,
  var userEntrega: String? = null,
  var userEntregaName: String? = null,
  var dataEntrega: LocalDate? = null,
  var horaEntrega: LocalTime? = null,
  var userRecebimentoNo: Int? = null,
  var userRecebimento: String? = null,
  var userRecebimentoName: String? = null,
  var dataRecebimento: LocalDate? = null,
  var horaRecebimento: LocalTime? = null,
) {
  var tipoDevEnum: ESolicitacaoTroca?
    get() = ESolicitacaoTroca.entries.firstOrNull { it.codigo == tipoDev }
    set(value) {
      tipoDev = value?.codigo
    }

  var produtoTrocaEnum: EProdutoTroca?
    get() = EProdutoTroca.entries.firstOrNull { it.codigo == produtoTroca }
    set(value) {
      produtoTroca = value?.codigo
    }

  val nfDevolucao: String
    get() {
      if (nfdno.isNullOrBlank()) {
        return ""
      }

      if (nfdse.isNullOrBlank()) {
        return nfdno ?: ""
      }

      return "$nfdno/$nfdse"
    }

  val quantidadeTotal
    get() = (quantidadeCom ?: 0) + (quantidadeSem ?: 0)

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
    if (produtoTrocaItemEnum != other.produtoTrocaItemEnum) return false

    return true
  }

  override fun hashCode(): Int {
    var result = ni ?: 0
    result = 31 * result + (prdno?.hashCode() ?: 0)
    result = 31 * result + (grade?.hashCode() ?: 0)
    result = 31 * result + (produtoTrocaItemEnum?.hashCode() ?: 0)
    return result
  }

  val codigo: Int?
    get() = prdno?.trim()?.toIntOrNull()

  val valorTotal: Double
    get() = (valorUnitario ?: 0.0) * (quantidadeDev ?: 0)

  val produtoTrocaItemEnum: EProdutoTroca?
    get() {
      val qtdCom = quantidadeCom ?: 0
      val qtdSem = quantidadeSem ?: 0
      return if (qtdCom > 0 && qtdSem > 0) {
        EProdutoTroca.Misto
      } else if (qtdCom > 0) {
        EProdutoTroca.Com
      } else if (qtdSem > 0) {
        EProdutoTroca.Sem
      } else {
        null
      }
    }

  val produtoTipo: String
    get() {
      val prdTroca = produtoTrocaItemEnum ?: return ""
      val tipoTroca = tipoDevEnum ?: return ""
      val sigla = when (prdTroca) {
        EProdutoTroca.Com   -> "P"
        EProdutoTroca.Sem   -> ""
        EProdutoTroca.Misto -> "M"
      }
      return "$tipoTroca $sigla".trim()
    }

  companion object {
    fun findAll(filtro: FiltroDadosDev): List<DadosDevProduto> {
      return saci.findDadosDev(filtro).explode()
    }
  }
}

private fun List<DadosDevProduto>.explode(): List<DadosDevProduto> {
  return this.flatMap { prd ->
    val tipo = prd.produtoTrocaItemEnum ?: return@flatMap emptyList()
    if (tipo == EProdutoTroca.Misto) {
      val prdCom = prd.copy(quantidadeSem = 0)
      val prdSem = prd.copy(quantidadeCom = 0)
      listOf(prdCom, prdSem)
    } else {
      listOf(prd)
    }
  }
}
