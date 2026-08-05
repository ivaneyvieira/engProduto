package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.rpad
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
  var autoriza: String?,
  var userTroca: Int?,
  var userSolicitacao: Int?,
  var motivoTroca: String?,
  var nfEntRet: Int?,
  var tipoNf: String?,
  var custnoVend: Int?,
  var filial: Int?,
  var nameFilial: String?,
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
  var xanoAutorizacao: Int?,
  var cancelado: Boolean?,
  var custnoCli: Int?,
  var custnoMuda: Int?,
  var custnoObs: Int?,
  var saldoDevolucaoCli: Double?,
  var saldoDevolucaoMuda: Double?,
  var nameCli: String?,
  var nameMuda: String?,
  var nameObs: String?,
) {
  val remarksLinha1: String
    get() = remarks?.rpad(80, " ")?.substring(0, 40)?.trim() ?: ""

  val remarksLinha2: String
    get() = remarks?.rpad(80, " ")?.substring(40, 80)?.trim() ?: ""

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

  var setMotivoTroca: Set<EMotivoTroca>
    get() = motivoTrocaCod?.split(";")?.mapNotNull { EMotivoTroca.find(it.trim()) }?.toSet().orEmpty()
    set(value) {
      motivoTrocaCod = value.joinToString(";") { it.codigo }
    }

  val strMotivoTroca: String
    get() = setMotivoTroca.sortedBy { it.codigo }.joinToString(", ") { it.descricao }

  var solicitacaoTrocaEnnum: ESolicitacaoTroca?
    get() = ESolicitacaoTroca.entries.firstOrNull { it.codigo == solicitacaoTroca }
    set(value) {
      solicitacaoTroca = value?.codigo
    }

  var produtoTrocaEnum: EProdutoTroca?
    get() = EProdutoTroca.entries.firstOrNull { it.codigo == produtoTroca }
    set(value) {
      produtoTroca = value?.codigo
    }

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

  fun produtos(): List<EntradaDevCliPro> {
    val dadosBruto = saci.entradaDevCliPro(invno)
    val dadosExplodidos = dadosBruto.explodeMisto()
    val dadosAjustados = dadosExplodidos.ajustaTipo(produtosAutorizacao())
    return dadosAjustados
  }

  private fun produtosAutorizacao(): List<ProdutoNFS> {
    return notaAutoriza().flatMap {
      it.produtos()
    }
  }

  private fun List<EntradaDevCliPro>.ajustaTipo(produtosAutorizacao: List<ProdutoNFS>): List<EntradaDevCliPro> {
    return this.map { prdCli ->
      if ((prdCli.tipo ?: "").endsWith("M")) {
        return@map prdCli
      }
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
    saci.marcaTrocaImpresso(
      invno = invno,
      storeno = storeno ?: 0,
      pdvno = pdvVenda ?: 0,
      xano = xano ?: 0,
      impressora = impressora
    )
    val lojaNaoInformado = saci.findLojaNaoInformada(custnoVend ?: 0)
    when {
      isReembolso()    -> {
        val saldoDevolucao = SaldoDevolucao(
          invno = invno,
          custnoDev = custnoVend ?: 0,
          custnoMuda = lojaNaoInformado?.codigo ?: 0,
          tipo = this.tipoObs,
          notaDev = this,
          saldo = valor ?: 0.00
        )
        saci.marcaReembolso(saldoDevolucao)
      }

      isMuda()         -> {
        val mudaCliente = mudaCodigo() ?: 0
        val custno = custnoVend ?: 0
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
        val mudaCliente = cliCodigo() ?: mudaCodigo() ?: 0
        val custno = filial ?: 0
        val saldoDevolucao = SaldoDevolucao(
          invno = invno,
          custnoDev = custno,
          custnoMuda = mudaCliente,
          tipo = this.tipoObs,
          saldo = valor ?: 0.00
        )
        saci.marcaMudaCliente(saldoDevolucao)
      }
    }
  }

  private fun isNaoInformado(): Boolean {
    return custnoVend == 200 || custnoVend == 300 || custnoVend == 400 || custnoVend == 500 || custnoVend == 800
  }

  private val MUDA_CLIENTE = "MUDA[^0-9]*([0-9]+)".toRegex()
  private val CLI_CLIENTE = "CLI[^0-9]*([0-9]+)".toRegex()

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

  private fun mudaCodigo(): Int? {
    val matchResult = MUDA_CLIENTE.find(remarks ?: "")
    return matchResult?.groupValues?.getOrNull(1)?.toIntOrNull()
  }

  private fun cliCodigo(): Int? {
    val matchResult = CLI_CLIENTE.find(remarks ?: "")
    return matchResult?.groupValues?.getOrNull(1)?.toIntOrNull()
  }

  fun mudaCliente(): String {
    val codigo = mudaCodigo() ?: 0
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
    val data = dataVenda ?: LocalDate.now()
    val dataCorte = data?.minusDays(30) ?: return emptyList()
    val dataInicial = data.minusDays(7)

    val filtroDefault = FiltroNotaVenda(
      loja = storenoAutorizacao ?: return emptyList(),
      pdv = pdvnoAutorizacao ?: return emptyList(),
      transacao = xanoAutorizacao ?: return emptyList(),
      pesquisa = "",
      invno = 0,
      dataInicial = user?.dataVendaDevolucao,
      dataFinal = nfData,
      dataCorte = user?.dataVendaDevolucao
    )
    val listaDefault = NotaVenda.findAll(filtroDefault)

    if (listaDefault.isNotEmpty()) {
      return listaDefault
    }

    val dePara = saci.deParaVendaEntrega(xanoAutorizacao ?: return emptyList(), dataCorte) ?: return emptyList()

    val filtro = FiltroNotaVenda(
      loja = dePara.loja ?: 0,
      pdv = dePara.pdv ?: 0,
      transacao = dePara.transacao ?: 0,
      pesquisa = "",
      invno = 0,
      dataInicial = user?.dataVendaDevolucao,
      dataFinal = nfData,
      dataCorte = user?.dataVendaDevolucao
    )
    val lista = NotaVenda.findAll(filtro)
    return lista.ifEmpty {
      val filtro = FiltroNotaVenda(
        loja = dePara.lojaE ?: 0,
        pdv = dePara.pdvE ?: 0,
        transacao = dePara.transacaoE ?: 0,
        pesquisa = "",
        invno = 0,
        dataInicial = user?.dataVendaDevolucao,
        dataFinal = nfData,
        dataCorte = user?.dataVendaDevolucao
      )
      val listaNova = NotaVenda.findAll(filtro).map { notaVenda ->
        notaVenda.loja = dePara.loja
        notaVenda.pdv = dePara.pdv
        notaVenda.transacao = dePara.transacao
        notaVenda.nota = dePara.notaVenda

        notaVenda.lojaE = dePara.lojaE
        notaVenda.pdvE = dePara.pdvE
        notaVenda.transacaoE = dePara.transacaoE
        notaVenda.notaEntrega = dePara.notaEntrega

        notaVenda
      }
      listaNova
    }
  }

  fun motivo(): String? {
    val produtoTroca = when (produtoTrocaEnum) {
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
    val tipo = this.solicitacaoTrocaEnnum ?: return true
    val produto = this.produtoTrocaEnum ?: return true
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

  fun desfazTroca() {
    //if (isNaoInformado() && impressora.isNullOrEmpty().not()) {
    saci.desmarcaTrocaImpresso(
      invno = invno,
      storeno = storeno ?: 0,
      pdvno = pdvVenda ?: 0,
      xano = xano ?: 0,
    )

    val mudaCliente = cliCodigo() ?: mudaCodigo() ?: 0
    val custno = filial ?: 0

    val saldoDevolucao = SaldoDevolucao(
      invno = invno,
      custnoDev = custno,
      custnoMuda = mudaCliente,
      tipo = this.tipoObs,
      saldo = -(valor ?: 0.00)
    )
    saci.marcaMudaCliente(saldoDevolucao)
    // }
  }

  fun validaTipoCredito(solicitacaoTrocaEnum: ESolicitacaoTroca) {
    if (tipoObs.startsWith(solicitacaoTrocaEnum.codigo).not()) {
      throw Exception("O tipo de crédito divergente da nota de devolução")
    }
  }

  fun validaTipoDevolucao(produtoTrocaEnum: EProdutoTroca) {
    val comProduto = tipoObs.contains(" P ") || tipoObs.endsWith(" P")
    val misto = tipoObs.contains(" M ") || tipoObs.endsWith(" M")

    if (misto) {
      if (produtoTrocaEnum != EProdutoTroca.Misto) {
        throw Exception("O tipo de devolução divergente da nota de devolução")
      }
    } else {
      if (comProduto) {
        if (produtoTrocaEnum != EProdutoTroca.Com) {
          throw Exception("O tipo de devolução divergente da nota de devolução")
        }
      } else {
        if (produtoTrocaEnum != EProdutoTroca.Sem) {
          throw Exception("O tipo de devolução divergente da nota de devolução")
        }
      }
    }
  }

  fun update() {
    saci.updateNotaVenda(this)
  }

  fun desfazSolicitacao() {
    saci.desfazSolidcitacaoDevolucao(this)
  }

  companion object {
    fun findAll(filtro: FiltroEntradaDevCli) = saci.entradaDevCli(filtro)
    fun findAllDevolucoes(filtro: FiltroEntradaDevCli) = saci.entradaDevCliDevolucoes(filtro)
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
  var dataCorte: LocalDate?,
  val cancelado: Boolean = false
)

enum class ETipoDevCli(val codigo: String) {
  COM("COM"), SEM("SEM"), TODOS("TODOS")
}