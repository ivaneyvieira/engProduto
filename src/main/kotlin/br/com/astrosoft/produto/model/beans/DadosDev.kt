package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class DadosDev(
  var ni: Int?,
  var loja: Int?,
  var nomeLoja: String?,
  var nfdno: String?,
  var nfdse: String?,
  var dataDevolucao: LocalDate?,
  var valorDev: Double?,
  var obs: String?,
  var nfVenda: String?,
  var obsNotaVenda: String?,
  var obsTipo: String?,
  var dataVenda: LocalDate?,
  var codCliente: Int?,
  var nomeCliente: String?,
  val produtos: List<DadosDevProduto>,
  /*DadosDevProduto?*/
  var nfno: Int?,
  var nfse: String?,
  var pdvno: Int?,
  var xano: Int?,
  var nfTipo: Int?,
  var empno: Int?,
  var custnoObs: Int?,
  var nomeClienteObs: String?,
  var vendedor: String?,
  var custnoVend: Int?,
  var nomeVend: String?,
  var notaEntrega: String?,
  var userSolicitacao: Int?,
  var loginSolicitacao: String?,
  var nomeSolicitacao: String?,
  var userTroca: Int?,
  var loginTroca: String?,
  var nomeTroca: String?,
  var produtoTroca: String?,
  var tipoDev: String?,
  var nfEntRet: Int?,
  var filial: Int?,
  var impressora: String,
  var fezTroca: String,
  var tipoNf: String,
  var tipoPgto: String,
) {
  val fezTrocaCol
    get() = if (fezTroca == "S") "Sim" else "Não"

  fun validaTipoCredito(solicitacaoTrocaEnum: ESolicitacaoTroca) {
    val tipo = this.obsTipo ?: throw Exception("Observação vazia")
    if (tipo.startsWith(solicitacaoTrocaEnum.codigo).not()) {
      throw Exception("O tipo de crédito divergente da nota de devolução")
    }
  }

  fun validaTipoDevolucao(produtoTrocaEnum: EProdutoTroca) {
    val tipo = obsTipo ?: throw Exception("Observação vazia")
    val comProduto = tipo.contains(" P ") || tipo.endsWith(" P")
    val misto = tipo.contains(" M ") || tipo.endsWith(" M")

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
    saci.updateDadosDev(this)
  }

  fun apagaDados() {
    saci.deleteDadosDev(this)
  }

  fun salvaNfEntRet() {
    saci.salvaNfEntRet(this)
  }

  private val MUDA_CLIENTE = "MUDA[^0-9]*([0-9]+)".toRegex()

  fun mudaCliente(): String {
    val codigo = this.custnoObs ?: 0
    val cliente = saci.mudaCliente(codigo) ?: return ""
    return "${cliente.codigo} - ${cliente.nome}"
  }

  fun isNaoInformado(): Boolean {
    return custnoVend == 200 || custnoVend == 300 || custnoVend == 400 || custnoVend == 500 || custnoVend == 800
  }

  fun marcaImpresso(impressora: Impressora) {
    val invno = ni ?: return
    saci.marcaTrocaImpresso(invno = invno, impressora = impressora)
    val lojaNaoInformado = saci.findLojaNaoInformada(custnoVend ?: 0)
    when {
      this.tipoDevEnum in listOf(ESolicitacaoTroca.Reembolso, ESolicitacaoTroca.Estorno) -> {
        val saldoDevolucao = SaldoDevolucao(
          invno = invno,
          custnoDev = custnoVend ?: 0,
          custnoMuda = lojaNaoInformado?.codigo ?: 0,
          tipo = this.obsTipo ?: "",
          notaDev = NotaVendaDados(
            loja = this.loja ?: 0,
            nfVenda = this.nfDevolucao,
            nfDev = this.nfVenda ?: ""
          ),
          saldo = this.valorDev ?: 0.00
        )
        saci.marcaReembolso(saldoDevolucao)
      }

      this.tipoDevEnum == ESolicitacaoTroca.MudaCliente       -> {
        val mudaCliente = custnoObs ?: 0
        val custno = custnoVend ?: 0
        val saldoDevolucao = SaldoDevolucao(
          invno = invno,
          custnoDev = custno,
          custnoMuda = mudaCliente,
          tipo = this.obsTipo ?: "",
          saldo = this.valorDev ?: 0.00
        )
        saci.marcaMudaCliente(saldoDevolucao)
      }

      isNaoInformado()                                        -> {
        val mudaCliente = custnoObs ?: 0
        val custno = filial ?: 0
        val saldoDevolucao = SaldoDevolucao(
          invno = invno,
          custnoDev = custno,
          custnoMuda = mudaCliente,
          tipo = this.obsTipo ?: "",
          saldo = this.valorDev ?: 0.00
        )
        saci.marcaMudaCliente(saldoDevolucao)
      }
    }
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

  companion object {
    fun findAll(filtro: FiltroDadosDev): List<DadosDev> {
      return saci.findDadosDev(filtro).toDadosDev()
    }
  }
}

private fun List<DadosDevProduto>.toDadosDev(): List<DadosDev> {
  return this.groupBy { it.ni }.mapNotNull {
    val lista = it.value
    val nota = lista.firstOrNull() ?: return@mapNotNull null
    DadosDev(
      ni = nota.ni,
      loja = nota.loja,
      nfdno = nota.nfdno,
      nfdse = nota.nfdse,
      dataDevolucao = nota.dataDevolucao,
      valorDev = nota.valorDev,
      obs = nota.obs,
      nfVenda = nota.nfVenda,
      obsTipo = nota.obsTipo,
      dataVenda = nota.dataVenda,
      codCliente = nota.codCliente,
      nomeCliente = nota.nomeCliente,
      produtos = lista,
      nfno = nota.nfno,
      nfse = nota.nfse,
      pdvno = nota.pdvno,
      xano = nota.xano,
      nfTipo = nota.nfTipo,
      vendedor = nota.vendedor,
      notaEntrega = nota.notaEntrega,
      userSolicitacao = nota.userSolicitacao,
      loginSolicitacao = nota.loginSolicitacao,
      nomeSolicitacao = nota.nomeSolicitacao,
      userTroca = nota.userTroca,
      loginTroca = nota.loginTroca,
      nomeTroca = nota.nomeTroca,
      produtoTroca = nota.produtoTroca,
      tipoDev = nota.tipoDev,
      nfEntRet = nota.nfEntRet,
      nomeLoja = nota.nomeLoja,
      obsNotaVenda = nota.obsNotaVenda,
      empno = nota.empno,
      custnoVend = nota.custnoVend,
      nomeVend = nota.nomeVend,
      custnoObs = nota.custnoObs,
      nomeClienteObs = nota.nomeClienteObs,
      filial = nota.filial,
      impressora = nota.impressora ?: "",
      fezTroca = nota.fezTroca ?: "",
      tipoNf = nota.tipoNf ?: "",
      tipoPgto = nota.tipoPgto ?: ""
    )
  }
}

data class FiltroDadosDev(
  val loja: Int,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val devolvido: Boolean = false,
  val localizacao: Set<String> = setOf("TODOS"),
  val impresso: Boolean?
)