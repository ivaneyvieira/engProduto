package br.com.astrosoft.produto.model.nfeXml

import br.com.astrosoft.framework.util.format
import com.fincatto.documentofiscal.DFBase
import com.fincatto.documentofiscal.nfe400.classes.NFModalidadeFrete
import com.fincatto.documentofiscal.nfe400.classes.nota.*
import com.fincatto.documentofiscal.utils.DFPersister
import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal

class ProdutoNotaEntradaVO(
  val id: Int,
  val xmlNfe: String?,
  val numeroProtocolo: String?,
  val dataHoraRecebimento: String?,
  val refNFe: String?,
) {
  fun produtosNotaEntradaNDD(): List<ProdutoNotaEntradaNdd> {
    xmlNfe ?: return emptyList()
    return try {
      val nota: NFNota = DFPersister(false).read(NFNota::class.java, xmlNfe)
      val produtosNota = nota.info?.itens ?: emptyList()
      produtosNota.mapNotNull(::mapProduto)
    } catch (e: Throwable) {
      emptyList()
    }
  }

  fun itensNotaReport(): List<ItensNotaReport> {
    return try {
      xmlNfe ?: return emptyList()
      println("XML FILE: $xmlNfe")
      val nota = DFPersister(false).read(NFNota::class.java, xmlNfe) ?: return emptyList()
      val data = dataHoraRecebimento?.split("T")?.getOrNull(0) ?: ""
      val hora = dataHoraRecebimento?.split("T")?.getOrNull(1)?.split("-")?.getOrNull(0) ?: ""
      val dataFormat = data.substring(8, 10) + "/" + data.substring(5, 7) + "/" + data.substring(0, 4)
      mapReport(nota, "$numeroProtocolo $dataFormat $hora")
    } catch (e: Throwable) {
      e.printStackTrace()
      emptyList()
    }
  }

  private fun mapProduto(item: NFNotaInfoItem): ProdutoNotaEntradaNdd {
    val produto: NFNotaInfoItemProduto? = item.produto
    val imposto: NFNotaInfoItemImposto? = item.imposto
    return ProdutoNotaEntradaNdd(
      id = id,
      numeroProtocolo = numeroProtocolo ?: "",
      codigo = produto?.codigo ?: "",
      codBarra = produto?.codigoDeBarrasGtin ?: "",
      descricao = produto?.descricao ?: "",
      ncm = produto?.ncm ?: "",
      cst = item.icms().cst() ?: "",
      cfop = produto?.cfop ?: "",
      un = produto?.unidadeComercial ?: "",
      quantidade = produto?.quantidadeComercial?.toDoubleOrNull() ?: 0.00,
      valorUnitario = produto?.valorUnitario?.toDoubleOrNull() ?: 0.00,
      valorTotal = produto?.valorTotalBruto?.toDoubleOrNull() ?: 0.00,
      baseICMS = imposto?.icms?.icms00?.valorBaseCalculo?.toDoubleOrNull() ?: 0.00,
      valorIPI = imposto?.ipi?.tributado?.valorTributo?.toDoubleOrNull() ?: 0.00,
      aliqICMS = imposto?.icms?.icms00?.percentualAliquota?.toDoubleOrNull() ?: 0.00,
      aliqIPI = imposto?.ipi?.tributado?.percentualAliquota?.toDoubleOrNull() ?: 0.00,
      valorOutros = produto?.valorOutrasDespesasAcessorias?.toDoubleOrNull() ?: 0.00,
      valorFrete = produto?.valorFrete?.toDoubleOrNull() ?: 0.00,
    )
  }

  companion object {
    fun mapReport(nota: NFNota, protocolo: String): List<ItensNotaReport> {
      val produtosNota = nota.info?.itens ?: emptyList()
      return produtosNota.mapNotNull { item ->
        ItensNotaReport(nota, protocolo, item)
      }
    }
  }
}

fun NFNotaInfoItem.icms(): DFBase? {
  val root = this.imposto.icms
  return root?.icms00 ?: root?.icms10 ?: root?.icms20 ?: root?.icms30 ?: root?.icms40 ?: root?.icms51 ?: root?.icms60
         ?: root?.icms70 ?: root?.icms90
}

fun DFBase?.percentualAliquota(): String? {
  this ?: return null
  return when (this) {
    is NFNotaInfoItemImpostoICMS00 -> this.percentualAliquota
    is NFNotaInfoItemImpostoICMS10 -> this.percentualAliquota
    is NFNotaInfoItemImpostoICMS20 -> this.percentualAliquota
    is NFNotaInfoItemImpostoICMS51 -> this.percentualICMS
    is NFNotaInfoItemImpostoICMS60 -> this.percentualAliquotaICMSSTSuportadaConsumidorFinal
    is NFNotaInfoItemImpostoICMS70 -> this.percentualAliquota
    is NFNotaInfoItemImpostoICMS90 -> this.percentualAliquota
    else                           -> null
  }
}

fun DFBase?.valorTributo(): String? {
  this ?: return null
  return when (this) {
    is NFNotaInfoItemImpostoICMS00 -> this.valorTributo
    is NFNotaInfoItemImpostoICMS10 -> this.valorTributo
    is NFNotaInfoItemImpostoICMS20 -> this.valorTributo
    is NFNotaInfoItemImpostoICMS51 -> this.valorICMSOperacao
    is NFNotaInfoItemImpostoICMS60 -> this.valorICMSSTRetido
    is NFNotaInfoItemImpostoICMS70 -> this.valorTributo
    is NFNotaInfoItemImpostoICMS90 -> this.valorTributo
    else                           -> null
  }
}

fun DFBase?.valorBaseCalculo(): String? {
  this ?: return null
  return when (this) {
    is NFNotaInfoItemImpostoICMS00 -> this.valorBaseCalculo
    is NFNotaInfoItemImpostoICMS10 -> this.valorBaseCalculo
    is NFNotaInfoItemImpostoICMS20 -> this.valorBCICMS
    is NFNotaInfoItemImpostoICMS30 -> this.valorBCICMSST
    is NFNotaInfoItemImpostoICMS51 -> this.valorBCICMS
    is NFNotaInfoItemImpostoICMS70 -> this.valorBC
    is NFNotaInfoItemImpostoICMS90 -> this.valorBC
    else                           -> null
  }
}

fun DFBase?.cst(): String? {
  this ?: return null
  return when (this) {
    is NFNotaInfoItemImpostoICMS00 -> this.origem?.codigo + this.situacaoTributaria?.codigo
    is NFNotaInfoItemImpostoICMS10 -> this.origem?.codigo + this.situacaoTributaria?.codigo
    is NFNotaInfoItemImpostoICMS20 -> this.origem?.codigo + this.situacaoTributaria?.codigo
    is NFNotaInfoItemImpostoICMS30 -> this.origem?.codigo + this.situacaoTributaria?.codigo
    is NFNotaInfoItemImpostoICMS40 -> this.origem?.codigo + this.situacaoTributaria?.codigo
    is NFNotaInfoItemImpostoICMS51 -> this.origem?.codigo + this.situacaoTributaria?.codigo
    is NFNotaInfoItemImpostoICMS60 -> this.origem?.codigo + this.situacaoTributaria?.codigo
    is NFNotaInfoItemImpostoICMS70 -> this.origem?.codigo + this.situacaoTributaria?.codigo
    is NFNotaInfoItemImpostoICMS90 -> this.origem?.codigo + this.situacaoTributaria?.codigo
    else                           -> null
  }
}

fun String?.formatInscricaoEstadual(): String {
  this ?: return ""
  return this
}

fun NFNotaInfoTransportador?.formatCpfj(): String {
  this ?: return ""
  return when {
    StringUtils.isNotBlank(cpf)  -> {
      cpf
    }

    StringUtils.isNotBlank(cnpj) -> {
      cnpj
    }

    else                         -> ""
  }.formatCpfj()
}

fun NFModalidadeFrete?.modalidadeFrete(): String {
  this ?: return ""
  return "$codigo-$descricao"
}

fun String?.formatCpfj(): String {
  this ?: return ""
  return when {
    matches("[0-9]{11}".toRegex()) -> "${substring(0, 3)}.${substring(3, 6)}.${substring(6, 9)}${substring(9, 11)}"
    matches("[0-9]{14}".toRegex()) -> {
      "${substring(0, 2)}.${substring(2, 5)}.${substring(5, 8)}/${substring(8, 12)}-${substring(12, 14)}"
    }

    else                           -> this
  }
}

fun String?.formatChaveAcesso(): String {
  this ?: return ""
  val parte01 = substring(0, 4)
  val parte02 = substring(4, 8)
  val parte03 = substring(8, 12)
  val parte04 = substring(12, 16)
  val parte05 = substring(16, 20)
  val parte06 = substring(20, 24)
  val parte07 = substring(24, 28)
  val parte08 = substring(28, 32)
  val parte09 = substring(32, 36)
  val parte10 = substring(36, 40)
  val parte11 = substring(40, 44)
  return "$parte01 $parte02 $parte03 $parte04 $parte05 $parte06 $parte07 $parte08 $parte09 $parte10 $parte11"
}

fun String?.formatTelefone(): String {
  this ?: return ""
  return when {
    this.matches("[0-9]{8}".toRegex()) -> this.substring(0, 5) + "-" + this.substring(5, 8)
    else                               -> this
  }
}

fun String?.formatCep(): String {
  this ?: return ""
  return when {
    matches("[0-9]{9}".toRegex())  -> "(${substring(0, 2)}) ${substring(2, 5)} ${substring(5, 9)}"
    matches("[0-9]{10}".toRegex()) -> "(${substring(0, 2)}) ${substring(2, 6)}-${substring(6, 10)}"
    matches("[0-9]{11}".toRegex()) -> "(${substring(0, 2)}) ${substring(2, 3)} ${substring(3, 7)}-${
      substring(
        7,
        11
      )
    }"

    else                           -> this
  }
}

fun String?.formatBigDecimal(): BigDecimal {
  return this?.toBigDecimalOrNull() ?: BigDecimal.ZERO
}

fun NFNotaInfoFatura.formatFatura(): String {
  val valorFatura = this.valorOriginalFatura?.toDoubleOrNull().format()
  val numeroFatura = this.numeroFatura ?: ""
  return "Fatura: $numeroFatura Valor: $valorFatura"
}

fun NFNotaInfoParcela.formatParcela(): String {
  return "Parcela: ${numeroParcela ?: ""} Vencimento: ${dataVencimento.format()} Valor: ${
    valorParcela.toDoubleOrNull().format()
  }"
}
