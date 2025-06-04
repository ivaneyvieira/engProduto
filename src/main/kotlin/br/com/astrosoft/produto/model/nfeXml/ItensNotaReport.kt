package br.com.astrosoft.produto.model.nfeXml

import br.com.astrosoft.framework.util.formatDate
import br.com.astrosoft.framework.util.formatTime
import com.fincatto.documentofiscal.nfe400.classes.nota.*
import java.math.BigDecimal

class ItensNotaReport(private val nota: NFNota, private val protocoloAlt: String, private val item: NFNotaInfoItem) :
  IItensNotaReport {
  private fun emitente(): NFNotaInfoEmitente? = nota.info?.emitente
  private fun destinatario(): NFNotaInfoDestinatario? = nota.info?.destinatario
  private fun transporte(): NFNotaInfoTransporte? = nota.info?.transporte
  private fun identificacao(): NFNotaInfoIdentificacao? = nota.info?.identificacao
  private fun icmsTotal(): NFNotaInfoICMSTotal? = nota.info?.total?.icmsTotal
  private fun issqnTotal(): NFNotaInfoISSQNTotal? = nota.info?.total?.issqnTotal

  override val nomeEmitente: String
    get() = emitente()?.razaoSocial ?: ""
  override val enderecoEmitente: String
    get() = "${emitente()?.endereco?.logradouro ?: ""}, ${emitente()?.endereco?.numero}"
  override val bairroEmitente: String
    get() = emitente()?.endereco?.bairro ?: ""
  override val cepEmitente: String
    get() = emitente()?.endereco?.cep.formatCep()
  override val cidadeEmitente: String
    get() = emitente()?.endereco?.descricaoMunicipio ?: ""
  override val ufEmitente: String
    get() = emitente()?.endereco?.uf ?: ""
  override val telefoneEmitente: String
    get() = emitente()?.endereco?.telefone.formatTelefone()
  override val entradaSaida: String
    get() = identificacao()?.tipo?.codigo ?: ""
  override val numeroNota: String
    get() = identificacao()?.numeroNota ?: ""
  override val serieNota: String
    get() = identificacao()?.serie ?: ""
  override val chaveAcesso: String
    get() = nota.info?.chaveAcesso.formatChaveAcesso()
  override val natureza: String
    get() = identificacao()?.naturezaOperacao ?: ""
  override val protocolo: String
    get() = protocoloAlt
  override val ieEmitente: String
    get() = emitente()?.inscricaoEstadual ?: ""
  override val ieSubstTrib: String
    get() = emitente()?.inscricaoEstadualSubstituicaoTributaria ?: ""
  override val cpnjEmitente: String
    get() = emitente()?.cpfj.formatCpfj()
  override val nomeDestinatario: String
    get() = destinatario()?.razaoSocial ?: ""
  override val cnpjCpfDestinatario: String
    get() = destinatario()?.cpfj.formatCpfj()
  override val dataEmissao: String
    get() = identificacao()?.dataHoraEmissao.formatDate()
  override val enderecoDestinatario: String
    get() = "${destinatario()?.endereco?.logradouro ?: ""}, ${destinatario()?.endereco?.numero} ${destinatario()?.endereco?.complemento ?: ""} "
  override val bairroDestinatario: String
    get() = destinatario()?.endereco?.bairro ?: ""
  override val cepDestinatario: String
    get() = destinatario()?.endereco?.cep ?: ""
  override val dataSaida: String
    get() = identificacao()?.dataHoraSaidaOuEntrada.formatDate()
  override val cidadeDestinatario: String
    get() = destinatario()?.endereco?.descricaoMunicipio ?: ""
  override val ufDestinatario: String
    get() = destinatario()?.endereco?.uf ?: ""
  override val telefoneDestinatario: String
    get() = destinatario()?.endereco?.telefone.formatTelefone()
  override val ieDestinatario: String
    get() = destinatario()?.inscricaoEstadual ?: ""
  override val horaSaida: String
    get() = identificacao()?.dataHoraSaidaOuEntrada.formatTime()
  override val faturaDuplicata: String
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
  override val bcICMS: BigDecimal
    get() = icmsTotal()?.baseCalculoICMS.formatBigDecimal()
  override val vlICMS: BigDecimal
    get() = icmsTotal()?.valorTotalICMS.formatBigDecimal()
  override val bcICMSSt: BigDecimal
    get() = icmsTotal()?.baseCalculoICMSST.formatBigDecimal()
  override val valorICMSSt: BigDecimal
    get() = icmsTotal()?.valorTotalICMSST.formatBigDecimal()
  override val vlProdutos: BigDecimal
    get() = icmsTotal()?.valorTotalDosProdutosServicos.formatBigDecimal()
  override val vlFrete: BigDecimal
    get() = icmsTotal()?.valorTotalFrete.formatBigDecimal()
  override val vlSeguro: BigDecimal
    get() = icmsTotal()?.valorTotalSeguro.formatBigDecimal()
  override val vlDesconto: BigDecimal
    get() = icmsTotal()?.valorTotalDesconto.formatBigDecimal()
  override val vlOutrasDesp: BigDecimal
    get() = icmsTotal()?.outrasDespesasAcessorias.formatBigDecimal()
  override val vlIPI: BigDecimal
    get() = icmsTotal()?.valorTotalIPI.formatBigDecimal()
  override val vlTrib: BigDecimal
    get() = icmsTotal()?.valorTotalTributos.formatBigDecimal()
  override val vlNota: BigDecimal
    get() = icmsTotal()?.valorTotalNFe.formatBigDecimal()
  override val nomeTransportadora: String
    get() = transporte()?.transportador?.razaoSocial ?: ""
  override val fretePorConta: String
    get() = transporte()?.modalidadeFrete.modalidadeFrete()
  override val codigoANTT: String
    get() = transporte()?.veiculo?.registroNacionalTransportadorCarga ?: ""
  override val nfo: String
    get() =  ""
  override val placaVeiculo: String
    get() = transporte()?.veiculo?.placaVeiculo ?: ""
  override val ufVeiculo: String
    get() = transporte()?.veiculo?.uf ?: ""
  override val cnpjCPF: String
    get() = transporte()?.transportador.formatCpfj()
  override val enderecoTransportadora: String
    get() = transporte()?.transportador?.enderecoComplemento ?: ""
  override val cidadeTransportadora: String
    get() = transporte()?.transportador?.nomeMunicipio ?: ""
  override val ufTransportadora: String
    get() = transporte()?.transportador?.uf ?: ""
  override val ieTransportadora: String
    get() = transporte()?.transportador?.inscricaoEstadual.formatInscricaoEstadual()
  override val quantidade: Long
    get() = transporte()?.volumes?.firstOrNull()?.quantidadeVolumesTransportados?.toLong() ?: 0
  override val especie: String
    get() = transporte()?.volumes?.firstOrNull()?.especieVolumesTransportados ?: ""
  override val marca: String
    get() = transporte()?.volumes?.firstOrNull()?.marca ?: ""
  override val numeracao: String
    get() = transporte()?.volumes?.firstOrNull()?.numeracaoVolumesTransportados ?: ""
  override val pesoBruto: BigDecimal
    get() = transporte()?.volumes?.firstOrNull()?.pesoBruto.formatBigDecimal()
  override val pesoLiquido: BigDecimal
    get() = transporte()?.volumes?.firstOrNull()?.pesoLiquido.formatBigDecimal()
  override val codigo: String
    get() = item.produto?.codigo ?: ""
  override val descricao: String
    get() = item.produto?.descricao ?: ""
  override val codigoBarras: String
    get() = item.produto?.codigoDeBarrasGtin ?: ""
  override val ncm: String
    get() = item.produto?.ncm ?: ""
  override val cst: String
    get() = item.icms().cst() ?: ""
  override val cfop: String
    get() = item.produto?.cfop ?: ""
  override val unidade: String
    get() = item.produto?.unidadeComercial ?: ""
  override val quantProduto: BigDecimal
    get() = item.produto?.quantidadeComercial.formatBigDecimal()
  override val valorUnitProduto: BigDecimal
    get() = item.produto?.valorUnitario.formatBigDecimal()
  override val valorTotalProduto: BigDecimal
    get() = item.produto?.valorTotalBruto.formatBigDecimal()
  override val bcICMSProduto: BigDecimal
    get() = item.icms()?.valorBaseCalculo().formatBigDecimal()
  override val vlICMSProduto: BigDecimal
    get() = item.icms()?.valorTributo().formatBigDecimal()
  override val vlIPIProduto: BigDecimal
    get() = item.imposto?.ipi?.tributado?.valorTributo.formatBigDecimal()
  override val aliqICMSProduto: BigDecimal
    get() = item.icms()?.percentualAliquota().formatBigDecimal()
  override val aliqIPIProduto: BigDecimal
    get() = item.imposto?.ipi?.tributado?.percentualAliquota.formatBigDecimal()
  override val inscricaoMunicial: String
    get() = emitente()?.inscricaoMunicipal ?: ""
  override val valorServicos: BigDecimal
    get() = issqnTotal()?.valorTotalServicosSobNaoIncidenciaNaoTributadosICMS.formatBigDecimal()
  override val bcISSQN: BigDecimal
    get() = issqnTotal()?.baseCalculoISS.formatBigDecimal()
  override val valorISSQN: BigDecimal
    get() = issqnTotal()?.valorTotalISS.formatBigDecimal()
  override val informacoesComplementares: String
    get() {
      val informacoesAdicionais = nota.info?.informacoesAdicionais ?: return ""
      val infAdFisco = informacoesAdicionais.informacoesAdicionaisInteresseFisco ?: ""
      val infCpl = informacoesAdicionais.informacoesComplementaresInteresseContribuinte ?: ""
      return "$infAdFisco\n$infCpl".trim().replace("^\\s*\n".toRegex(), "")
    }
}