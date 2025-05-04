package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaRecebimentoDev(
  var loja: Int?,
  var lojaSigla: String?,
  var data: LocalDate?,
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
  private var dadosNotas: DadosNotaSaida? = null

  val natureza
    get() = dadosNotas?.natureza

  val pedido
    get() = dadosNotas?.pedido

  val dataPedido
    get() = dadosNotas?.dataPedido.format()

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

  fun marcaSituacao(situacao: EStituacaoDev) {
    this.situacaoDev = situacao.num
    save()
  }

  fun delete() {
    saci.removerNotaRecebimentoDev(this)
  }

  fun updateDadosNota() {
    val listDadosNotas = saci.findDadosNota(storeno, pdvno, xano)
    this.dadosNotas = listDadosNotas.firstOrNull()
    produtos.forEach { prd ->
      prd.listDadosNotas = listDadosNotas.filter { it.prdno == prd.prdno && it.grade == prd.grade }
    }
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
        data = nota.data,
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

