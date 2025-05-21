package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaRecebimentoDev(
  var loja: Int?,
  var lojaSigla: String?,
  var dataEntrada: LocalDate?,
  var emissao: LocalDate?,
  val niPrincipal: Int?,
  var niList: List<Int>,
  var numeroDevolucao: Int?,
  var nfEntrada: String?,
  var custno: Int?,
  var vendno: Int?,
  var vendnoProduto: Int?,
  var fornecedor: String?,
  var valorNF: Double?,
  var pedComp: Int?,
  var transp: Int?,
  var transportadora: String?,
  var cte: Int?,
  var dataDevolucao: LocalDate?,
  var volume: Int?,
  var peso: Double?,
  var usernoRecebe: Int?,
  var usuarioRecebe: String?,
  var observacaoNota: String?,
  var tipoNota: String?,
  var tipoDevolucao: Int?,
  var pesoDevolucao: Double?,
  var volumeDevolucao: Int?,
  var transpDevolucao: Int?,
  var transportadoraDevolucao: String?,
  var cteDevolucao: String?,
  var situacaoDev: Int?,
  var userDevolucao: String?,
  var notaDevolucao: String?,
  var emissaoDevolucao: LocalDate?,
  var valorDevolucao: Double?,
  var obsDevolucao: String?,
  var observacaoDev: String?,
  var storeno: Int?,
  var pdvno: Int?,
  var xano: Int?,
  var dataColeta: LocalDate?,
  var observacaoAdicional: String?,
  var countColeta: Int?,
  var countArq: Int?,
  var produtos: List<NotaRecebimentoProdutoDev>,
) {
  val baseIcmsProdutos
    get() = produtos.sumOf { it.baseIcmsDevolucao ?: 0.00 }

  val valorIcmsProdutos
    get() = produtos.sumOf { it.valIcmsDevolucao ?: 0.00 }

  val baseIcmsSubstProduto
    get() = produtos.sumOf { it.baseIcmsSubst }

  val icmsSubstProduto
    get() = produtos.sumOf { it.icmsSubstDevolucao ?: 0.00 }

  val valorTotalProduto
    get() = produtos.sumOf { it.valorTotalDevolucao }

  val valorFrete
    get() = 0.00

  val valorSeguro
    get() = 0.00

  val valorDesconto
    get() = produtos.sumOf { it.valorDescontoDevolucao ?: 0.00 }

  val outrasDespesas
    get() = produtos.sumOf { it.outDespDevolucao ?: 0.00 }

  val valorIpiProdutos
    get() = produtos.sumOf { it.valIPIDevolucao ?: 0.00 }

  val valorTotalNota
    get() = icmsSubstProduto + valorFrete + valorSeguro - valorDesconto + valorTotalProduto + outrasDespesas + valorIpiProdutos

  val vendnoNF: Int?
    get() = if (tipoDevolucaoEnun?.fob == true) {
      transpDevolucao ?: transp
    } else {
      vendno
    }

  val fornecedorNF: String?
    get() = if (tipoDevolucaoEnun?.fob == true) {
      transportadoraDevolucao ?: transportadora
    } else {
      fornecedor
    }

  val niListStr
    get() = niList.joinToString(separator = ", ") {
      it.toString()
    }
  val valorNFDevolucao
    get() = produtos.sumOf { it.totalGeralDevolucao }

  var tipoDevolucaoEnun
    get() = ETipoDevolucao.findByNum(tipoDevolucao ?: 0)
    set(value) {
      tipoDevolucao = value?.num
    }

  val tipoDevolucaoName
    get() = tipoDevolucaoEnun?.descricao

  fun produtosCodigoBarras(codigoBarra: String?): NotaRecebimentoProdutoDev? {
    if (codigoBarra.isNullOrBlank()) return null
    return produtos.firstOrNull { it.containBarcode(codigoBarra) }
  }

  fun refreshProdutosDev(): NotaRecebimentoDev? {
    val notaRefresh = findAllDev(
      FiltroNotaRecebimentoProdutoDev(
        loja = this.loja ?: return null,
        pesquisa = "",
      ),
      EStituacaoDev.entries.firstOrNull { it.num == situacaoDev } ?: EStituacaoDev.PENDENCIA
    ).firstOrNull {
      it.numeroDevolucao == this.numeroDevolucao
    }
    this.produtos = notaRefresh?.produtos ?: emptyList()
    return notaRefresh
  }

  fun listArquivos(): List<InvFileDev> {
    val niList = this.niList
    val tipo = ETipoDevolucao.findByNum(tipoDevolucao ?: 0) ?: return emptyList()
    val numero = this.numeroDevolucao ?: return emptyList()
    return niList.flatMap { invno -> InvFileDev.findAll(invno, tipo, numero) }
  }

  fun save() {
    val userno = AppConfig.userLogin()?.no ?: 0
    saci.saveInvAdicional(this, userno)
  }

  fun salvaMotivoDevolucao(tipoDevolucaoNovo: Int) {
    saci.salvaMotivoDevolucao(this, tipoDevolucaoNovo)
  }

  fun marcaSituacao(situacao: EStituacaoDev) {
    this.situacaoDev = situacao.num
    save()
  }

  fun delete() {
    saci.removerNotaRecebimentoDev(this)
  }

  companion object {
    fun findAllDev(
      filtro: FiltroNotaRecebimentoProdutoDev,
      situacaoDev: EStituacaoDev,
    ): List<NotaRecebimentoDev> {
      val filtroTodos = filtro.copy()
      return saci.findNotaRecebimentoProdutoDev(filtroTodos, situacaoDev.num).toNota()
        .filter { nota ->
          ((nota.tipoDevolucao ?: 0) > 0)
        }.filter {
          val pesquisa = filtro.pesquisa
          (pesquisa == "") ||
          (it.tipoDevolucaoEnun?.descricao?.startsWith(pesquisa, ignoreCase = true) == true) ||
          (it.vendno?.toString() == pesquisa) ||
          (it.fornecedor?.contains(pesquisa, ignoreCase = true) == true) ||
          (it.niPrincipal?.toString()?.contains(pesquisa, ignoreCase = true) == true)
        }
    }
  }
}

fun List<NotaRecebimentoProdutoDev>.toNota(): List<NotaRecebimentoDev> {
  return this.groupBy { it.chaveDevolucao }.mapNotNull { entry ->
    val produtos = entry.value.distinctBy { "${it.codigo}${it.grade}" }
    val nota = produtos.firstOrNull { it.notaDevolucao != null }
               ?: produtos.firstOrNull { it.notaDevolucao == null }

    nota?.let {
      NotaRecebimentoDev(
        loja = nota.loja,
        dataEntrada = nota.dataEntrada,
        emissao = nota.emissao,
        numeroDevolucao = nota.numeroDevolucao,
        niPrincipal = nota.ni,
        niList = produtos.mapNotNull { it.ni }.sorted().distinct(),
        nfEntrada = nota.nfEntrada,
        custno = nota.custno,
        vendno = nota.vendno,
        fornecedor = nota.fornecedor,
        valorNF = nota.valorNF,
        pedComp = nota.pedComp,
        transp = nota.transp,
        cte = nota.cte,
        volume = nota.volume,
        peso = nota.peso,
        produtos = produtos,
        vendnoProduto = produtos.groupBy { it.vendnoProduto }.entries.minByOrNull {
          -it.value.size
        }?.key,
        usernoRecebe = produtos.firstOrNull { it.usernoRecebe != 0 }?.usernoRecebe,
        usuarioRecebe = produtos.filter { !it.usuarioRecebe.isNullOrBlank() }.mapNotNull { it.usuarioRecebe }.distinct()
          .joinToString(),
        observacaoNota = nota.observacaoNota,
        tipoNota = nota.tipoNota,
        lojaSigla = nota.lojaSigla,
        transportadora = nota.transportadora,
        tipoDevolucao = nota.tipoDevolucao ?: 0,
        pesoDevolucao = nota.pesoDevolucao ?: 0.00,
        volumeDevolucao = nota.volumeDevolucao ?: 0,
        transpDevolucao = nota.transpDevolucao,
        cteDevolucao = nota.cteDevolucao,
        situacaoDev = nota.situacaoDev,
        userDevolucao = nota.userDevolucao,
        notaDevolucao = nota.notaDevolucao,
        emissaoDevolucao = nota.emissaoDevolucao,
        valorDevolucao = nota.valorDevolucao,
        obsDevolucao = nota.obsDevolucao,
        dataDevolucao = nota.dataDevolucao,
        observacaoDev = nota.observacaoDev,
        dataColeta = nota.dataColeta,
        observacaoAdicional = nota.observacaoAdicional,
        countColeta = nota.countColeta,
        countArq = nota.countArq,
        storeno = nota.storeno,
        pdvno = nota.pdvno,
        xano = nota.xano,
        transportadoraDevolucao = nota.transportadoraDevolucao,
      )
    }
  }
}

