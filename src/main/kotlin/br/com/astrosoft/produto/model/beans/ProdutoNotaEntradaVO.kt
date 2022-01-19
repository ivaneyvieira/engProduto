package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.formatDate
import br.com.astrosoft.framework.util.formatTime
import com.fincatto.documentofiscal.DFBase
import com.fincatto.documentofiscal.nfe400.classes.NFModalidadeFrete
import com.fincatto.documentofiscal.nfe400.classes.nota.*
import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal

class ProdutoNotaEntradaVO(
  val id: Int,
  val xmlNfe: String,
  val numeroProtocolo: String,
  val dataHoraRecebimento: String,
  val refNFe: String?,
) {
  companion object {
    fun mapReport(nota: NFNota, protocolo: String): List<ItensNotaReport> {
      val produtosNota = nota.info?.itens ?: emptyList()
      return produtosNota.mapNotNull { item ->
        ItensNotaReport(nota, protocolo, item)
      }
    }
  }
}

class ItensNotaReport(private val nota: NFNota, private val protocoloAlt: String, private val item: NFNotaInfoItem) {
  private fun emitente(): NFNotaInfoEmitente? = nota.info?.emitente
  private fun destinatario(): NFNotaInfoDestinatario? = nota.info?.destinatario
  private fun transporte(): NFNotaInfoTransporte? = nota.info?.transporte
  private fun identificacao(): NFNotaInfoIdentificacao? = nota.info?.identificacao
  private fun icmsTotal(): NFNotaInfoICMSTotal? = nota.info?.total?.icmsTotal
  private fun issqnTotal(): NFNotaInfoISSQNTotal? = nota.info?.total?.issqnTotal

  val nomeEmitente: String
    get() = emitente()?.razaoSocial ?: ""
  val enderecoEmitente: String
    get() = "${emitente()?.endereco?.logradouro ?: ""}, ${emitente()?.endereco?.numero}"
  val bairroEmitente: String
    get() = emitente()?.endereco?.bairro ?: ""
  val cepEmitente: String
    get() = emitente()?.endereco?.cep.formatCep()
  val cidadeEmitente: String
    get() = emitente()?.endereco?.descricaoMunicipio ?: ""
  val ufEmitente: String
    get() = emitente()?.endereco?.uf ?: ""
  val telefoneEmitente: String
    get() = emitente()?.endereco?.telefone.formatTelefone()
  val entradaSaida: String
    get() = identificacao()?.tipo?.codigo ?: ""
  val numeroNota: String
    get() = identificacao()?.numeroNota ?: ""
  val serieNota: String
    get() = identificacao()?.serie ?: ""
  val chaveAcesso: String
    get() = nota.info?.chaveAcesso.formatChaveAcesso()
  val natureza: String
    get() = identificacao()?.naturezaOperacao ?: ""
  val protocolo: String
    get() = protocoloAlt
  val ieEmitente: String
    get() = emitente()?.inscricaoEstadual ?: ""
  val ieSubstTrib: String
    get() = emitente()?.inscricaoEstadualSubstituicaoTributaria ?: ""
  val cpnjEmitente: String
    get() = emitente()?.cpfj.formatCpfj()
  val nomeDestinatario: String
    get() = destinatario()?.razaoSocial ?: ""
  val cnpjCpfDestinatario: String
    get() = destinatario()?.cpfj.formatCpfj()
  val dataEmissao: String
    get() = identificacao()?.dataHoraEmissao.formatDate()
  val enderecoDestinatario: String
    get() = "${destinatario()?.endereco?.logradouro ?: ""}, ${destinatario()?.endereco?.numero}"
  val bairroDestinatario: String
    get() = destinatario()?.endereco?.bairro ?: ""
  val cepDestinatario: String
    get() = destinatario()?.endereco?.cep ?: ""
  val dataSaida: String
    get() = identificacao()?.dataHoraSaidaOuEntrada.formatDate()
  val cidadeDestinatario: String
    get() = destinatario()?.endereco?.codigoMunicipio ?: ""
  val ufDestinatario: String
    get() = destinatario()?.endereco?.uf ?: ""
  val telefoneDestinatario: String
    get() = destinatario()?.endereco?.telefone.formatTelefone()
  val ieDestinatario: String
    get() = destinatario()?.inscricaoEstadual ?: ""
  val horaSaida: String
    get() = identificacao()?.dataHoraSaidaOuEntrada.formatTime()
  val faturaDuplicata: String
    get() {
      val cobranca = nota.info?.cobranca ?: return ""
      val fatura = cobranca.fatura ?: return ""
      val parcelas = cobranca.parcelas ?: return ""
      val labelFatura = fatura.formatFatura()
      val labelParcleas = parcelas.mapNotNull { parcela ->
        parcela.formatParcela()
      }.joinToString(" / ")
      return "$labelFatura $labelParcleas"
    }
  val bcICMS: BigDecimal
    get() = icmsTotal()?.baseCalculoICMS.formatBigDecimal()
  val vlICMS: BigDecimal
    get() = icmsTotal()?.valorTotalICMS.formatBigDecimal()
  val bcICMSSt: BigDecimal
    get() = icmsTotal()?.baseCalculoICMSST.formatBigDecimal()
  val valorICMSSt: BigDecimal
    get() = icmsTotal()?.valorTotalICMSST.formatBigDecimal()
  val vlProdutos: BigDecimal
    get() = icmsTotal()?.valorTotalDosProdutosServicos.formatBigDecimal()
  val vlFrete: BigDecimal
    get() = icmsTotal()?.valorTotalFrete.formatBigDecimal()
  val vlSeguro: BigDecimal
    get() = icmsTotal()?.valorTotalSeguro.formatBigDecimal()
  val vlDesconto: BigDecimal
    get() = icmsTotal()?.valorTotalDesconto.formatBigDecimal()
  val vlOutrasDesp: BigDecimal
    get() = icmsTotal()?.outrasDespesasAcessorias.formatBigDecimal()
  val vlIPI: BigDecimal
    get() = icmsTotal()?.valorTotalIPI.formatBigDecimal()
  val vlTrib: BigDecimal
    get() = icmsTotal()?.valorTotalTributos.formatBigDecimal()
  val vlNota: BigDecimal
    get() = icmsTotal()?.valorTotalNFe.formatBigDecimal()
  val nomeTransportadora: String
    get() = transporte()?.transportador?.razaoSocial ?: ""
  val fretePorConta: String
    get() = transporte()?.modalidadeFrete.modalidadeFrete()
  val codigoANTT: String
    get() = transporte()?.veiculo?.registroNacionalTransportadorCarga ?: ""
  val placaVeiculo: String
    get() = transporte()?.veiculo?.placaVeiculo ?: ""
  val ufVeiculo: String
    get() = transporte()?.veiculo?.uf ?: ""
  val cnpjCPF: String
    get() = transporte()?.transportador.formatCpfj()
  val enderecoTransportadora: String
    get() = transporte()?.transportador?.enderecoComplemento ?: ""
  val cidadeTransportadora: String
    get() = transporte()?.transportador?.nomeMunicipio ?: ""
  val ufTransportadora: String
    get() = transporte()?.transportador?.uf ?: ""
  val ieTransportadora: String
    get() = transporte()?.transportador?.inscricaoEstadual.formatInscricaoEstadual()
  val quantidade: Long
    get() = transporte()?.volumes?.firstOrNull()?.quantidadeVolumesTransportados?.toLong() ?: 0
  val especie: String
    get() = transporte()?.volumes?.firstOrNull()?.especieVolumesTransportados ?: ""
  val marca: String
    get() = transporte()?.volumes?.firstOrNull()?.marca ?: ""
  val numeracao: String
    get() = transporte()?.volumes?.firstOrNull()?.numeracaoVolumesTransportados ?: ""
  val pesoBruto: BigDecimal
    get() = transporte()?.volumes?.firstOrNull()?.pesoBruto.formatBigDecimal()
  val pesoLiquido: BigDecimal
    get() = transporte()?.volumes?.firstOrNull()?.pesoLiquido.formatBigDecimal()
  val codigo: String
    get() = item.produto?.codigo ?: ""
  val descricao: String
    get() = item.produto?.descricao ?: ""
  val codigoBarras: String
    get() = item.produto?.codigoDeBarras ?: ""
  val ncm: String
    get() = item.produto?.ncm ?: ""
  val cst: String
    get() = item.icms().cst() ?: ""
  val cfop: String
    get() = item.produto?.cfop ?: ""
  val unidade: String
    get() = item.produto?.unidadeComercial ?: ""
  val quantProduto: BigDecimal
    get() = item.produto?.quantidadeComercial.formatBigDecimal()
  val valorUnitProduto: BigDecimal
    get() = item.produto?.valorUnitario.formatBigDecimal()
  val valorTotalProduto: BigDecimal
    get() = item.produto?.valorTotalBruto.formatBigDecimal()
  val bcICMSProduto: BigDecimal
    get() = item.icms()?.valorBaseCalculo().formatBigDecimal()
  val vlICMSProduto: BigDecimal
    get() = item.icms()?.valorTributo().formatBigDecimal()
  val vlIPIProduto: BigDecimal
    get() = item.imposto?.ipi?.tributado?.valorTributo.formatBigDecimal()
  val aliqICMSProduto: BigDecimal
    get() = item.icms()?.percentualAliquota().formatBigDecimal()
  val aliqIPIProduto: BigDecimal
    get() = item.imposto?.ipi?.tributado?.percentualAliquota.formatBigDecimal()
  val inscricaoMunicial: String
    get() = emitente()?.inscricaoMunicipal ?: ""
  val valorServicos: BigDecimal
    get() = issqnTotal()?.valorTotalServicosSobNaoIncidenciaNaoTributadosICMS.formatBigDecimal()
  val bcISSQN: BigDecimal
    get() = issqnTotal()?.baseCalculoISS.formatBigDecimal()
  val valorISSQN: BigDecimal
    get() = issqnTotal()?.valorTotalISS.formatBigDecimal()
  val informacoesComplementares: String
    get() {
      val informacoesAdicionais = nota.info?.informacoesAdicionais ?: return ""
      val infAdFisco = informacoesAdicionais.informacoesAdicionaisInteresseFisco ?: ""
      val infCpl = informacoesAdicionais.informacoesComplementaresInteresseContribuinte ?: ""
      return "$infAdFisco\n$infCpl".trim().replace("^[\\s]*\n".toRegex(), "")
    }
}

private fun NFNotaInfoItem.icms(): DFBase? {
  val root = this.imposto.icms
  return root?.icms00 ?: root?.icms10 ?: root?.icms20 ?: root?.icms30 ?: root?.icms40 ?: root?.icms51 ?: root?.icms60
  ?: root?.icms70 ?: root?.icms90
}

private fun DFBase?.percentualAliquota(): String? {
  this ?: return null
  return when (this) {
    is NFNotaInfoItemImpostoICMS00 -> this.percentualAliquota
    is NFNotaInfoItemImpostoICMS10 -> this.percentualAliquota
    is NFNotaInfoItemImpostoICMS20 -> this.percentualAliquota
    is NFNotaInfoItemImpostoICMS51 -> this.percentualICMS
    is NFNotaInfoItemImpostoICMS60 -> this.percentualAliquotaICMSSTSuportadaConsumidorFinal
    is NFNotaInfoItemImpostoICMS70 -> this.percentualAliquota
    is NFNotaInfoItemImpostoICMS90 -> this.percentualAliquota
    else -> null
  }
}

private fun DFBase?.valorTributo(): String? {
  this ?: return null
  return when (this) {
    is NFNotaInfoItemImpostoICMS00 -> this.valorTributo
    is NFNotaInfoItemImpostoICMS10 -> this.valorTributo
    is NFNotaInfoItemImpostoICMS20 -> this.valorTributo
    is NFNotaInfoItemImpostoICMS51 -> this.valorICMSOperacao
    is NFNotaInfoItemImpostoICMS60 -> this.valorICMSSTRetido
    is NFNotaInfoItemImpostoICMS70 -> this.valorTributo
    is NFNotaInfoItemImpostoICMS90 -> this.valorTributo
    else -> null
  }
}

private fun DFBase?.valorBaseCalculo(): String? {
  this ?: return null
  return when (this) {
    is NFNotaInfoItemImpostoICMS00 -> this.valorBaseCalculo
    is NFNotaInfoItemImpostoICMS10 -> this.valorBaseCalculo
    is NFNotaInfoItemImpostoICMS20 -> this.valorBCICMS
    is NFNotaInfoItemImpostoICMS30 -> this.valorBCICMSST
    is NFNotaInfoItemImpostoICMS51 -> this.valorBCICMS
    is NFNotaInfoItemImpostoICMS70 -> this.valorBC
    is NFNotaInfoItemImpostoICMS90 -> this.valorBC
    else -> null
  }
}

private fun DFBase?.cst(): String? {
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
    else -> null
  }
}

private fun String?.formatInscricaoEstadual(): String {
  this ?: return ""
  return this
}

private fun NFNotaInfoTransportador?.formatCpfj(): String {
  this ?: return ""
  return when {
    StringUtils.isNotBlank(cpf) -> {
      cpf
    }
    StringUtils.isNotBlank(cnpj) -> {
      cnpj
    }
    else -> ""
  }.formatCpfj()
}

private fun NFModalidadeFrete?.modalidadeFrete(): String {
  this ?: return ""
  return "$codigo-$descricao"
}

private fun String?.formatCpfj(): String {
  this ?: return ""
  return when {
    matches("[0-9]{11}".toRegex()) -> "${substring(0, 3)}.${substring(3, 6)}.${substring(6, 9)}${substring(9, 11)}"
    matches("[0-9]{14}".toRegex()) -> {
      "${substring(0, 2)}.${substring(2, 5)}.${substring(5, 8)}/${substring(8, 12)}-${substring(12, 14)}"
    }
    else -> this
  }
}

private fun String?.formatChaveAcesso(): String {
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

private fun String?.formatTelefone(): String {
  this ?: return ""
  return when {
    this.matches("[0-9]{8}".toRegex()) -> this.substring(0, 5) + "-" + this.substring(5, 8)
    else -> this
  }
}

private fun String?.formatCep(): String {
  this ?: return ""
  return when {
    matches("[0-9]{9}".toRegex()) -> "(${substring(0, 2)}) ${substring(2, 5)} ${substring(5, 9)}"
    matches("[0-9]{10}".toRegex()) -> "(${substring(0, 2)}) ${substring(2, 6)}-${substring(6, 10)}"
    matches("[0-9]{11}".toRegex()) -> "(${substring(0, 2)}) ${substring(2, 3)} ${substring(3, 7)}-${substring(7, 11)}"
    else -> this
  }
}

private fun String?.formatBigDecimal(): BigDecimal {
  return this?.toBigDecimalOrNull() ?: BigDecimal.ZERO
}

private fun NFNotaInfoFatura.formatFatura(): String {
  val valorFatura = this.valorOriginalFatura?.toDoubleOrNull().format()
  val numeroFatura = this.numeroFatura ?: ""
  return "Fatura: $numeroFatura Valor: $valorFatura"
}

private fun NFNotaInfoParcela.formatParcela(): String {
  return "Parcela: ${numeroParcela ?: ""} Vencimento: ${dataVencimento.format()} Valor: ${
    valorParcela.toDoubleOrNull().format()
  }"
}
