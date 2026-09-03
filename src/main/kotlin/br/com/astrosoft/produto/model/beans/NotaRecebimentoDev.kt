package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.rpad
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import kotlin.math.ceil

class NotaRecebimentoDev(
  var loja: Int?,
  var lojaSigla: String?,
  var dataEntrada: LocalDate?,
  var emissao: LocalDate?,
  val niPrincipal: Int?,
  val nfdstnr: Boolean,
  val freteNota: Double?,
  var niList: List<Int>,
  var numeroDevolucao: Int?,
  var niDev: Int?,
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
  var motivoDevolucao: Int?,
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
  var nfdRecusa: String?,
  var nfRetorno: String?,
  var emissaoRetorno: LocalDate?,
  var niRetorno: Int?,
  var observacaoAdicional: String?,
  var countColeta: Int?,
  var countArq: Int?,
  var duplicata: String?,
  var dataVencimentoDup: LocalDate?,
  var valorVencimentoDup: Double?,
  var volumeNFDevolucao: Int?,
  var transpNFDevolucao: Int?,
  var pesoNFBrutoDevolucao: Double?,
  var pesoNFLiquidoDevolucao: Double?,
  var produtos: List<NotaRecebimentoProdutoDev>,
  var situacaoDup: String?,
  var duplicataNum: String?,
  var situacaoDupStatus: Int?,
  var obsDup: String?,
  var obsNF: String?
) {
  val chaveNotaSaida
    get() = "$storeno $pdvno $xano"
  
  var obsNFQuebra: String
    get() {
      val obs = obsNF ?: ""
      val list40 = buildList {
        val qtLines = ceil(obs.length / 40.00).toInt()
        repeat(qtLines) {
          val start = it * 40
          val end = if (it == qtLines - 1) obs.length else (it + 1) * 40
          add(obs.substring(start, end).trim())
        }
      }
      return list40.joinToString("\n")
    }
    set(value) {
      val linhas = value.split("\n")
      obsNF = linhas.joinToString("") { it.rpad(40, " ") }
    }

  val dataColetaStr: String
    get() {
      val notaNFD = notaDevolucao ?: ""
      return if (notaNFD.isNotBlank() && dataColeta == null) {
        "Pendente"
      } else {
        dataColeta?.format("dd/MM/yyyy") ?: ""
      }
    }

  val totalProdutosVenda
    get() = produtos.sumOf { prd ->
      (prd.quantDevolucao ?: 0) * (prd.precoVenda ?: 0.00)
    }

  val chaveEmail: String
    get() = produtos.firstOrNull()?.chaveDevolucao ?: ""

  fun listEmail(): List<EmailDevolucao> {
      val chave = chaveEmail
      val listEmail = EmailDevolucao.findAll(chave)
      return listEmail
    }

  val nomeTransportadoraDevolucao: String
    get() {
      val vendno = transpDevolucao ?: return ""
      return saci.findTransportadora(vendno)?.nome ?: ""
    }

  val situacaoDevName
    get() = produtos.mapNotNull {
      EStituacaoDev.findByNum(it.situacaoDev ?: 0)?.descricao
    }.distinct().joinToString(", ")

  //********* Diferenças ********

  fun diferenca(): Boolean {
    return diferencaVolume() || diferencaPeso() || diferencaTransp()
  }

  fun diferencaVolume(): Boolean {
    if (notaDevolucao.isNullOrBlank()) return false
    return (volumeDevolucao ?: 0) != (volumeNFDevolucao ?: 0)
  }

  fun diferencaPeso(): Boolean {
    if (notaDevolucao.isNullOrBlank()) return false
    return (pesoDevolucao ?: 0.00).format("0.0000") != (pesoNFBrutoDevolucao ?: 0.00).format("0.0000")
  }

  fun diferencaTransp(): Boolean {
    if (notaDevolucao.isNullOrBlank()) return false
    return (transpDevolucao ?: 0) != (transpNFDevolucao ?: 0)
  }

  //*****************************

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
    get() = if (motivoDevolucaoEnun?.fob == true) {
      transpDevolucao ?: transp
    } else {
      vendno
    }

  val fornecedorNF: String?
    get() = if (motivoDevolucaoEnun?.fob == true) {
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

  var motivoDevolucaoEnun
    get() = EMotivoDevolucao.findByNum(motivoDevolucao ?: 0)
    set(value) {
      motivoDevolucao = value?.num
    }

  val motivoDevolucaoName: String
    get() = motivoDevolucaoEnun?.descricao ?: ""

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
      EStituacaoDev.list().firstOrNull { it.num == situacaoDev } ?: EStituacaoDev.PEDIDO
    ).firstOrNull {
      it.numeroDevolucao == this.numeroDevolucao
    }
    this.produtos = notaRefresh?.produtos ?: emptyList()
    return notaRefresh
  }

  fun listArquivos(): List<InvFileDev> {
    val niList = this.niList
    val tipo = EMotivoDevolucao.findByNum(motivoDevolucao ?: 0) ?: return emptyList()
    val numero = this.numeroDevolucao ?: return emptyList()
    return niList.flatMap { invno -> InvFileDev.findAll(invno, tipo, numero) }
  }

  fun save() {
    val userno = AppConfig.userLogin()?.no ?: 0
    saci.saveInvAdicional(this, userno)
  }

  fun salvaMotivoDevolucao(motivoDevolucaoNovo: Int) {
    saci.salvaMotivoDevolucao(this, motivoDevolucaoNovo)
  }

  fun marcaSituacao(situacao: EStituacaoDev) {
    this.situacaoDev = situacao.num
    save()
  }

  fun delete() {
    if (motivoDevolucaoEnun?.notasMultiplas == true) {
      saci.removerNotaRecebimentoDevMult(this)
    } else {
      saci.removerNotaRecebimentoDevSimples(this)
    }
    produtos.forEach {
      it.deleteProduto()
    }
  }

  fun listRepresentantes(): List<Representante> {
    val vendno = this.vendno ?: return emptyList()
    return saci.representante(vendno)
  }

  fun salvaObservacao() {
    saci.salvaObservacao(this)
  }

  fun contaChave(): ChaveEmail {
    return saci.countChaveEmail(chaveEmail)
  }
  
  fun addNotaSaida(nfSaida: String) {
    val loja = this.loja ?: throw Exception("Loja não encontrada")
    val niDev = this.niDev ?: throw Exception("Numero da devolução não encontrado")
    val numero = nfSaida.split("/").getOrNull(0) ?: throw Exception("Numero da nota não encontrada")
    val serie = nfSaida.split("/").getOrNull(1) ?: throw Exception("Série da nota não encontrada")
    val notaSaida = saci.localizaNotaSaida(loja, numero, serie) ?: throw Exception("Nota de saída não encontrada")
    this.storeno = notaSaida.storeno
    this.pdvno = notaSaida.pdvno
    this.xano = notaSaida.xano
    saci.adicionaNIDev(notaSaida, niDev)
  }
  
  companion object {
    fun findAllDev(
      filtro: FiltroNotaRecebimentoProdutoDev,
      situacaoDev: EStituacaoDev,
      divergencia: Boolean = false,
    ): List<NotaRecebimentoDev> {
      val filtroTodos = filtro.copy()
      return saci.findNotaRecebimentoProdutoDev(filtro = filtroTodos, situacaoDev = situacaoDev.num).toNota()
        .filter { nota ->
          ((nota.motivoDevolucao ?: 0) > 0)
        }.filter {
          val pesquisa = filtro.pesquisa
          (pesquisa == "") ||
          (it.motivoDevolucaoEnun?.descricao?.startsWith(pesquisa, ignoreCase = true) == true) ||
          (it.vendno?.toString() == pesquisa) ||
          (it.fornecedor?.contains(pesquisa, ignoreCase = true) == true) ||
          (it.niPrincipal?.toString()?.contains(pesquisa, ignoreCase = true) == true) ||
          (it.numeroDevolucao?.toString() == pesquisa)
        }.filter {
          if (divergencia) {
            (it.motivoDevolucaoEnun?.divergente == true) && ((it.situacaoDev ?: 0) == EStituacaoDev.PEDIDO.num)
          } else {
            (it.motivoDevolucaoEnun?.divergente == false) || ((it.situacaoDev ?: 0) != EStituacaoDev.PEDIDO.num)
          }
        }.filter {
          !filtro.nfdstnr || it.nfdstnr
        }
    }
  }
}

fun List<NotaRecebimentoProdutoDev>.toNota(): List<NotaRecebimentoDev> {
  return this.groupBy { it.chaveDevolucao }.mapNotNull { entry ->
    val produtos = entry.value.distinctBy { "${it.ni}${it.codigo}${it.grade}${it.seq}" }
    val seqMax = produtos.maxOfOrNull { it.seq ?: 0 } ?: 0
    produtos.sortedBy {
      "${it.codigo}${it.grade}${it.ni}"
    }.filter {
      it.seq == null
    }.forEachIndexed { index, produto ->
      produto.saveSeq(seqMax + index + 1)
    }
    val nota = produtos.minByOrNull { it.seq ?: 99999 }

    nota?.let {
      NotaRecebimentoDev(
        loja = nota.loja,
        dataEntrada = nota.dataEntrada,
        emissao = nota.emissao,
        numeroDevolucao = nota.numeroDevolucao,
        niPrincipal = nota.ni,
        freteNota = nota.freteNota,
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
        motivoDevolucao = nota.motivoDevolucao ?: 0,
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
        nfdRecusa = nota.nfdRecusa,
        nfRetorno = nota.nfRetorno,
        emissaoRetorno = nota.emissaoRetorno,
        niRetorno = nota.niRetorno,
        observacaoAdicional = nota.observacaoAdicional,
        countColeta = nota.countColeta,
        countArq = nota.countArq,
        storeno = nota.storeno,
        pdvno = nota.pdvno,
        xano = nota.xano,
        duplicata = nota.duplicata,
        dataVencimentoDup = nota.dataVencimentoDup,
        valorVencimentoDup = nota.valorVencimentoDup,
        transportadoraDevolucao = nota.transportadoraDevolucao,
        volumeNFDevolucao = nota.volumeNFDevolucao,
        transpNFDevolucao = nota.transpNFDevolucao,
        pesoNFBrutoDevolucao = nota.pesoNFBrutoDevolucao,
        pesoNFLiquidoDevolucao = nota.pesoNFLiquidoDevolucao,
        nfdstnr = produtos.any { it.taxno == "06" && it.cst == "000" },
        situacaoDup = nota.situacaoDup,
        duplicataNum = nota.duplicataNum,
        situacaoDupStatus = nota.situacaoDupStatus,
        obsDup = nota.obsDup,
        obsNF = nota.obsNF,
        niDev = nota.niDev,
      )
    }
  }
}

enum class EStatusDup(val codigo: Int, val descricao: String) {
  CANCELADA(5, "Cancelada"),
  EM_COBRANCA(1, "Em cobrança"),
  INCLUIDA(0, "Incluída"),
  PENDENTE(999, "Pendente"),
  QUITADA(2, "Quitada"),
}