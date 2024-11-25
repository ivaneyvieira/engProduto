package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.saci
import br.com.astrosoft.produto.nfeXml.icms.tags
import com.fincatto.documentofiscal.nfe400.classes.nota.*
import com.fincatto.documentofiscal.utils.DFPersister
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class ConsultaNfeFile(private val notaEntradaXML: NotaEntradaXML) {
  private var nfNota: NFNota? = null

  init {
    val xmlString = notaEntradaXML.xmlNfe ?: ""
    nfNota = DFPersister(false).read(NFNota::class.java, xmlString)
  }

  val inv2Parameters: Inv2Parameters
    get() {
      return Inv2Parameters(
        invno = saci.proximoNumeroInvno2(),
        vendno = notaEntradaXML.fornecedorNota ?: 0,
        ordno = notaEntradaXML.pedido,
        issueDate = notaEntradaXML.dataEmissao.toSaciDate(),
        dataSaida = notaEntradaXML.dataEmissao.toSaciDate(),
        freight = nfNota?.icmsTotal?.valorTotalFrete.toSaciValor(),
        baseCalculo = nfNota?.icmsTotal?.baseCalculoICMS.toSaciValor(),
        grossamt = notaEntradaXML.valorTotal.toSaciValor(),
        substTrib = nfNota?.icmsTotal?.baseCalculoICMSST.toSaciValor(),
        discount = nfNota?.icmsTotal?.valorTotalDesconto.toSaciValor(),
        prdamt = notaEntradaXML.valorTotalProdutos.toSaciValor(),
        despesas = 0,
        weight = nfNota?.volumes.orEmpty().sumOf { it?.pesoBruto?.toDoubleOrNull() ?: 0.00 },
        ipi = nfNota?.icmsTotal?.valorTotalIPI.toSaciValor(),
        icm = nfNota?.icmsTotal?.valorTotalICMS.toSaciValor(),
        baseIpi = nfNota?.info?.itens.orEmpty().sumOf { it.imposto?.ipi?.tributado?.valorBaseCalculo.toSaciValor() },
        aliq = nfNota?.info?.itens.orEmpty().firstNotNullOfOrNull { it.imposto?.icms?.percentualAliquota }?.toInt()
               ?: 0,
        cfo = nfNota?.itens.orEmpty().firstNotNullOfOrNull { it?.cfop }?.toInt() ?: 0,
        icmsUfRemet = 0,
        icmsDese = nfNota?.icmsTotal?.valorICMSDesonerado.toSaciValor(),
        conhecimentoFrete = 0,
        carrno = saci.findCarr(nfNota?.info?.transporte?.transportador?.cnpj ?: ""),
        packages = nfNota?.volumes?.size ?: 0,
        storeno = notaEntradaXML.loja,
        bookBits = 0,
        l3 = 0,
        nfname = notaEntradaXML.numero.toString(),
        invse = notaEntradaXML.serie.toString()
      )
    }

  private val produtosPedido = notaEntradaXML.produtosPedido()

  private fun ProdutoNotaEntradaNdd.produtosPedido(): PedidoXML? {
    val pedido = produtosPedido.firstOrNull {
      it.refFor == this.codigo
    } ?: produtosPedido.firstOrNull {
      it.barcode == this.codBarra
    }
    return pedido
  }

  fun iprd2Parameters(inv2Param: Inv2Parameters): List<Iprd2Parameters> {
    val produtos = notaEntradaXML.produtosNdd().map { ndd ->
      ndd.pedidoXML = ndd.produtosPedido()
      ndd
    }
    val itens = nfNota?.info?.itens.orEmpty()
    val invnoNota = inv2Param.invno
    val storenoNota = inv2Param.storeno
    var seqItem = 1
    return produtos.filter { prd ->
      prd.codigoPedido != null
    }.mapNotNull { ndd ->
      itens.firstOrNull { it.produto?.codigo == ndd.codigo }?.let { item ->
        val valorConvPedido = ndd.valorConvPedido ?: 0.00
        Iprd2Parameters(
          // Primary Keys and related fields
          invno = invnoNota,
          prdno = ndd.pedidoXML?.prdno ?: "",
          grade = ndd.pedidoXML?.grade ?: "",

          // Quantities and costs
          qtty = ndd.quantConvPedido?.roundToInt() ?: 0,
          fob = (valorConvPedido * 100).roundToLong(),
          cost = (valorConvPedido * 100).roundToLong(),
          fob4 = (valorConvPedido * 10000).roundToLong(),
          cost4 = (valorConvPedido * 10000).roundToLong(),
          dfob = valorConvPedido,

          // Impostos - ICMS
          icms = item.imposto?.icms?.tags()?.vICMS.toSaciValor(),
          icmsAliq = item.imposto?.icms?.tags()?.pICMS.toSaciValor().toInt(),
          baseIcms = item.imposto?.icms?.tags()?.vBC.toSaciValor(),
          baseIcmsSubst = item.imposto?.icms?.tags()?.vBCST.toSaciValor(),
          icmsSubst = item.imposto?.icms?.tags()?.vICMSST.toSaciValor(),
          reducaoBaseIcms = item.imposto?.icms?.tags()?.pRedBCST.toSaciValor(),
          amtCredIcmsSN = item.imposto?.icms?.tags()?.vCredICMSSN.toSaciValor(),
          amtIcmsDeson = item.imposto?.icms?.tags()?.vICMSDeson.toSaciValor(),
          amtIcmsDifer = item.imposto?.icms?.tags()?.vICMSDif.toSaciValor(),
          amtIcmsEfet = item.imposto?.icms?.tags()?.vICMSEfet.toSaciValor(),
          aliqIcmsDifer = item.imposto?.icms?.tags()?.pDif.toSaciValor().toInt(),
          aliqIcmsEfet = item.imposto?.icms?.tags()?.pICMSEfet.toSaciValor().toInt(),
          aliqIcmsInter = item.imposto?.icms?.tags()?.zero.toSaciValor().toInt(),
          aliqIcmsPart = item.imposto?.icms?.tags()?.zero.toSaciValor().toInt(),
          aliqIcmsUfDest = item.imposto?.icms?.tags()?.zero.toSaciValor().toInt(),

          // Impostos - FCP
          amtFcpSt = item.imposto?.icms?.tags()?.vFCPST.toSaciValor(),
          amtFcpStRet = item.imposto?.icms?.tags()?.vFCPSTRet.toSaciValor(),
          amtFcpUfDest = item.imposto?.icms?.tags()?.zero.toSaciValor(),
          baseFcp = item.imposto?.icms?.tags()?.vBCFCP.toSaciValor(),
          baseFcpSt = item.imposto?.icms?.tags()?.vBCFCPST.toSaciValor(),
          baseFcpStRet = item.imposto?.icms?.tags()?.vBCFCPSTRet.toSaciValor(),
          baseFcpUfDest = item.imposto?.icms?.tags()?.zero.toSaciValor(),
          aliqFcp = item.imposto?.icms?.tags()?.pFCP.toSaciValor().toInt(),
          aliqFcpSt = item.imposto?.icms?.tags()?.pFCPST.toSaciValor().toInt(),
          aliqFcpStRet = item.imposto?.icms?.tags()?.pFCPSTRet.toSaciValor().toInt(),
          aliqFcpUfDest = item.imposto?.icms?.tags()?.zero.toSaciValor().toInt(),

          // Impostos - IPI
          ipi = item.imposto?.ipi?.tributado?.percentualAliquota.toSaciValor().toInt(),
          ipiAmt = item.imposto?.ipi?.tributado?.valorTributo.toSaciValor(),
          baseIpi = item.imposto?.ipi?.tributado?.valorBaseCalculo.toSaciValor(),

          // Discount and other fields
          discount = 0,
          lucroTributado = 0,

          // Store and Sequence
          storeno = storenoNota,
          seq = seqItem++,

          // Flags
          motIcmsDeson = item.imposto?.icms?.tags()?.zero.toSaciValor().toInt(),
          percBaseOper = item.imposto?.icms?.tags()?.zero.toSaciValor().toInt(),
          percCredSN = item.imposto?.icms?.tags()?.pCredSN.toSaciValor().toInt(),
          percRedIcmsEfet = item.imposto?.icms?.tags()?.pRedBCEfet.toSaciValor().toInt(),
          percRedIcmsSt = item.imposto?.icms?.tags()?.pRedBCST.toSaciValor().toInt(),

          // Product and classification fields
          cstIcms = item.imposto?.icms?.tags()?.origCST() ?: "",
          cstIpi = item.imposto?.ipi?.tributado?.situacaoTributaria?.codigo
                   ?: item.imposto?.ipi?.naoTributado?.situacaoTributaria?.codigo ?: ""
        )
      }
    }
  }
}

private fun String?.toSaciValor(): Long {
  this ?: return 0
  val valor = this.toDoubleOrNull() ?: 0.0
  return (valor * 100).toLong()
}

private fun Double?.toSaciValor(): Long {
  this ?: return 0
  return (this * 100).toLong()
}

private val NFNota.icmsTotal: NFNotaInfoICMSTotal?
  get() {
    return this.info?.total?.icmsTotal
  }

private val NFNota.itens: List<NFNotaInfoItemProduto?>
  get() {
    return this.info?.itens.orEmpty().map { it.produto }
  }

private val NFNota.volumes: List<NFNotaInfoVolume?>?
  get() {
    return this.info?.transporte?.volumes
  }

private val NFNotaInfoItemImpostoICMS.icmsProperty: String
  get() {
    return icms00?.toString() ?: icms02?.toString() ?: icms10?.toString() ?: icms15?.toString() ?: icms20?.toString()
           ?: icms30?.toString() ?: icms40?.toString() ?: icms51?.toString() ?: icms53?.toString() ?: icms60?.toString()
           ?: icms61?.toString() ?: icms70?.toString() ?: icms90?.toString() ?: icmsPartilhado?.toString()
           ?: icmsst?.toString() ?: icmssn101?.toString() ?: icmssn102?.toString() ?: icmssn201?.toString()
           ?: icmssn202?.toString() ?: icmssn500?.toString() ?: icmssn900?.toString() ?: ""
  }

private val NFNotaInfoItemImpostoICMS.baseCalculoICMS: Long
  get() {
    return icms00?.valorBaseCalculo?.toSaciValor()
           ?: icms10?.valorBaseCalculo?.toSaciValor()
           ?: icms20?.valorBCICMS?.toSaciValor()
           ?: icms51?.valorBCICMS?.toSaciValor()
           ?: icms70?.valorBC?.toSaciValor()
           ?: icms90?.valorBC?.toSaciValor()
           ?: icmsPartilhado?.valorBCICMS?.toSaciValor()
           ?: icmssn900?.valorBCICMS?.toSaciValor() ?: 0

  }

private val NFNotaInfoItemImpostoICMS.valorICMS: Long
  get() {
    return icms00?.valorTributo?.toSaciValor()
           ?: icms10?.valorTributo?.toSaciValor()
           ?: icms20?.valorTributo?.toSaciValor()
           ?: icms51?.valorICMS?.toSaciValor()
           ?: icms70?.valorTributo?.toSaciValor()
           ?: icms90?.valorTributo?.toSaciValor()
           ?: icmsPartilhado?.valorICMS?.toSaciValor()
           ?: icmssn900?.valorICMS?.toSaciValor() ?: 0

  }

private val NFNotaInfoItemImpostoICMS.percentualAliquota: Long
  get() {
    return icms00?.percentualAliquota?.toSaciValor()
           ?: icms10?.percentualAliquota?.toSaciValor()
           ?: icms20?.percentualAliquota?.toSaciValor()
           ?: icms51?.percentualICMS?.toSaciValor()
           ?: icms70?.percentualAliquota?.toSaciValor()
           ?: icms90?.percentualAliquota?.toSaciValor()
           ?: icmsPartilhado?.percentualAliquotaImposto?.toSaciValor()
           ?: icmssn900?.percentualAliquotaImposto?.toSaciValor() ?: 0
  }