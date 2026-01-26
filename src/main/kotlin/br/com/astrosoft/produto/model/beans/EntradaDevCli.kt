package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class EntradaDevCli(
  val invno: Int,
  var loja: Int,
  var nomeLoja: String?,
  var notaFiscal: String?,
  var data: LocalDate?,
  var hora: String?,
  var vendno: Int?,
  var fornecedor: String?,
  var custnoDev: Int?,
  var clienteDev: String?,
  var remarks: String?,
  var valor: Double?,
  var storeno: Int?,
  var pdvno: Int?,
  var xano: Int?,
  var custno: Int?,
  var nfVenda: String?,
  var nfData: LocalDate?,
  var nfValor: Double?,
  var cliente: String?,
  var cfo: Int?,
  var empno: Int?,
  var vendedor: String?,
  var impressora: String?,
  var userName: String?,
  var userLogin: String?,
  var pdvVenda: Int?,
  var nfVendaVenda: String?,
  var dataVenda: LocalDate?,
  var clienteVenda: Int?,
  var clienteNome: String?,
  var nfValorVenda: Double?,
  var fezTroca: String?,
  var usernoAutorizacao: Int?,
  var nameAutorizacao: String?,
  var loginAutorizacao: String?,
  var usernoSolicitacao: Int?,
  var nameSolicitacao: String?,
  var loginSolicitacao: String?,
  var comProduto: String?,
  var solicitacaoTroca: String?,
  var produtoTroca: String?,
  var motivoTrocaCod: String?,
  var liberaImpressao: String?,
  var storenoAutorizacao: Int?,
  var pdvnoAutorizacao: Int?,
  var xanoAutorizacao: Int?
) {
  var liberaStr: String
    get() = when (liberaImpressao) {
      "S"  -> "Sim"
      "N"  -> "Não"
      else -> "Não"
    }
    set(value) {
      liberaImpressao = when (value) {
        "Sim" -> "S"
        "Não" -> "N"
        else  -> "N"
      }
    }

  val setMotivoTroca: Set<EMotivoTroca>
    get() = motivoTrocaCod?.split(";")?.mapNotNull { EMotivoTroca.find(it.trim()) }?.toSet().orEmpty()

  val strMotivoTroca: String
    get() = setMotivoTroca.sortedBy { it.codigo }.joinToString(", ") { it.descricao }

  val solicitacaoTrocaEnnum: ESolicitacaoTroca?
    get() = ESolicitacaoTroca.entries.firstOrNull { it.codigo == solicitacaoTroca }

  val produtoTrocaEnnum: EProdutoTroca?
    get() = EProdutoTroca.entries.firstOrNull { it.codigo == produtoTroca }

  val fezTrocaCol
    get() = if (fezTroca == "S") "Sim" else "Não"

  val observacao: String
    get() {
      val parte1 = remarks?.split(")")?.getOrNull(0) ?: return ""
      return "$parte1)"
    }

  val tipoObs: String
    get() {
      val parte2 = remarks?.split(")")?.getOrNull(1) ?: return ""
      return parte2.trim()
    }

  fun produtos() = saci.entradaDevCliPro(invno).explodeMisto().ajustaTipo(produtosAutoriacao())

  private fun produtosAutoriacao(): List<ProdutoNFS> {
    return notaAutoriza().flatMap {
      it.produtos()
    }
  }

  private fun List<EntradaDevCliPro>.ajustaTipo(produtosAutorizacao: List<ProdutoNFS>): List<EntradaDevCliPro> {
    return this.map { prdCli ->
      val produtoAut = produtosAutorizacao.firstOrNull { prdAut ->
        prdCli.prdno == prdAut.prdno && prdCli.grade == prdAut.grade
      }

      if (produtoAut == null) {
        return@map prdCli
      }

      prdCli.copy(
        tipoPrd = produtoAut.tipoPrd()
      )
    }
  }

  fun marcaImpresso(impressora: Impressora) {
    saci.marcaImpresso(
      invno = invno,
      storeno = storeno ?: 0,
      pdvno = pdvVenda ?: 0,
      xano = xano ?: 0,
      impressora = impressora
    )
    val lojaNaoInformado = saci.findLojaNaoInformada(custno ?: 0)
    when {
      isReembolso()    -> {
        val saldoDevolucao = SaldoDevolucao(
          invno = invno,
          custnoDev = custno ?: 0,
          custnoMuda = lojaNaoInformado?.codigo ?: 0,
          tipo = this.tipoObs,
          notaDev = this,
          saldo = valor ?: 0.00
        )
        saci.marcaReembolso(saldoDevolucao)
      }

      isMuda()         -> {
        val mudaCliente = mudaCodigo()
        val custno = custno ?: 0
        val saldoDevolucao = SaldoDevolucao(
          invno = invno,
          custnoDev = custno,
          custnoMuda = mudaCliente,
          tipo = this.tipoObs,
          saldo = valor ?: 0.00
        )
        saci.marcaMudaCliente(saldoDevolucao)
      }

      isNaoInformado() -> {
        // val saldoDevolucao = SaldoDevolucao(
        //   invno = invno,
        //   custnoDev = lojaNaoInformado?.codigo ?: 0,
        //   custnoMuda = custno ?: 0,
        //   saldo = valor ?: 0.00
        // )
        //saci.marcaMudaCliente(saldoDevolucao)
        println("Não faz nada")
      }
    }
  }

  private fun isNaoInformado(): Boolean {
    return custno == 200 || custno == 300 || custno == 400 || custno == 500 || custno == 800
  }

  private val MUDA_CLIENTE = "MUDA[^0-9]*([0-9]+)".toRegex()

  fun isReembolso(): Boolean {
    return remarks?.contains("EST CARTAO", ignoreCase = true) == true ||
           remarks?.contains("EST BOLETO", ignoreCase = true) == true ||
           remarks?.contains("REEMBOLSO", ignoreCase = true) == true ||
           remarks?.contains("GARANTIA", ignoreCase = true) == true ||
           remarks?.contains("EST DEP", ignoreCase = true) == true
  }

  private fun isMuda(): Boolean {
    return remarks?.contains(MUDA_CLIENTE) == true
  }

  fun isComProduto(): Boolean {
    return comProduto == "COM"
  }

  private fun mudaCodigo(): Int {
    val matchResult = MUDA_CLIENTE.find(remarks ?: "")
    return matchResult?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
  }

  fun mudaCliente(): String {
    val codigo = mudaCodigo()
    val cliente = saci.mudaCliente(codigo) ?: return ""
    return "${cliente.codigo} - ${cliente.nome}"
  }

  fun autoriza(user: UserSaci) {
    saci.autorizaNota(
      invno = invno,
      storeno = storeno ?: 0,
      pdvno = pdvno ?: 0,
      xano = xano ?: 0,
      user = user
    )
  }

  fun isTipoMisto(): Boolean {
    return "TRO.* M.*".toRegex().matches(this.tipoObs) ||
           "EST.* M.*".toRegex().matches(this.tipoObs) ||
           "REE.* M.*".toRegex().matches(this.tipoObs)
  }

  fun notaAutoriza(): List<NotaVenda> {
    val user = AppConfig.userLogin() as? UserSaci
    val filtro = FiltroNotaVenda(
      loja = storenoAutorizacao ?: loja,
      pdv = pdvnoAutorizacao ?: 0,
      transacao = xanoAutorizacao ?: return emptyList(),
      pesquisa = "",
      invno = 0,
      dataInicial = user?.dataVendaDevolucao,
      dataFinal = nfData,
      dataCorte = user?.dataVendaDevolucao
    )
    return NotaVenda.findAll(filtro)
  }

  fun motivo(): String? {
    val produtoTroca = when (produtoTrocaEnnum) {
      EProdutoTroca.Com   -> "P"
      EProdutoTroca.Sem   -> ""
      EProdutoTroca.Misto -> "M"
      null                -> return null
    }

    val solicitacaoTroca = when (solicitacaoTrocaEnnum) {
      ESolicitacaoTroca.Troca       -> "Troca"
      ESolicitacaoTroca.Estorno     -> "Estorno"
      ESolicitacaoTroca.Reembolso   -> "Reembolso"
      ESolicitacaoTroca.MudaCliente -> "Muda"
      null                          -> return null
    }

    return "$solicitacaoTroca $produtoTroca".trim()
  }

  fun salvaLiberaPedido() {
    saci.salvaLiberaPedido(this)
  }

  fun naoLiberado(): Boolean {
    val nota = notaAutoriza().firstOrNull() ?: return true
    val tipo = nota.solicitacaoTrocaEnnum ?: return true
    val produto = nota.produtoTrocaEnnum ?: return true
    val tipoOk = tipo == ESolicitacaoTroca.Estorno ||
                 tipo == ESolicitacaoTroca.MudaCliente ||
                 tipo == ESolicitacaoTroca.Reembolso ||
                 produto == EProdutoTroca.Sem ||
                 produto == EProdutoTroca.Misto
    return if (tipoOk) {
      liberaImpressao == "N" || liberaImpressao == "" || liberaImpressao == null
    } else {
      false
    }
  }

  companion object {
    fun findAll(filtro: FiltroEntradaDevCli) = saci.entradaDevCli(filtro)
  }
}

data class FiltroEntradaDevCli(
  val loja: Int,
  val query: String,
  val dataI: LocalDate?,
  val dataF: LocalDate?,
  val dataLimiteInicial: LocalDate?,
  val impresso: Boolean?,
  val tipo: ETipoDevCli,
  var dataCorte: LocalDate?
)

enum class ETipoDevCli(val codigo: String) {
  COM("COM"), SEM("SEM"), TODOS("TODOS")
}