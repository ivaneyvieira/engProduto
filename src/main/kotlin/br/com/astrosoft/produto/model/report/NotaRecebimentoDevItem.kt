package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev
import br.com.astrosoft.produto.model.nfeXml.IItensNotaReport
import br.com.astrosoft.produto.model.saci
import java.math.BigDecimal

class NotaRecebimentoDevItem(val nota: NotaRecebimentoDev, val produto: NotaRecebimentoProdutoDev): IItensNotaReport {
  val loja = saci.findLoja(nota.loja)
  val fornecedor = saci.findFornecedor(nota.vendno)
  val transportadora = saci.findTransportadora(nota.transp ?: 0)

  override val nomeEmitente: String
    get() = loja?.name ?: ""
  override val enderecoEmitente: String
    get() = loja?.addr ?: ""
  override val bairroEmitente: String
    get() = loja?.nei ?: ""
  override val cepEmitente: String
    get() = loja?.zip ?: ""
  override val cidadeEmitente: String
    get() = loja?.city ?: ""
  override val ufEmitente: String
    get() = loja?.state ?: ""
  override val telefoneEmitente: String
    get() = "(${loja?.ddd ?: ""}) ${loja?.tel ?: ""}"
  override val entradaSaida: String
    get() = "1"
  override val numeroNota: String
    get() = nota.notaDevolucao?.split("/")?.getOrNull(0) ?: ""
  override val serieNota: String
    get() = nota.notaDevolucao?.split("/")?.getOrNull(1) ?: ""
  override val chaveAcesso: String
    get() = "00000000000000000000000000000000000000000000"
  override val natureza: String
    get() = "Devolução"
  override val protocolo: String
    get() = ""
  override val ieEmitente: String
    get() = loja?.insc ?: ""
  override val ieSubstTrib: String
    get() = "SEM VALOR"
  override val cpnjEmitente: String
    get() = loja?.cgc ?: ""
  override val nomeDestinatario: String
    get() = nota.fornecedor ?: ""
  override val cnpjCpfDestinatario: String
    get() = fornecedor?.cgc ?: ""
  override val dataEmissao: String
    get() = nota.emissaoDevolucao.format()
  override val enderecoDestinatario: String
    get() = fornecedor?.addr ?: ""
  override val bairroDestinatario: String
    get() = fornecedor?.nei ?: ""
  override val cepDestinatario: String
    get() = fornecedor?.zip ?: ""
  override val dataSaida: String
    get() = nota.dataDevolucao.format() 
  override val cidadeDestinatario: String
    get() = fornecedor?.city ?: ""
  override val ufDestinatario: String
    get() = fornecedor?.state ?: ""
  override val telefoneDestinatario: String
    get() = "(${fornecedor?.ddd ?: ""}) ${fornecedor?.tel ?: ""}"
  override val ieDestinatario: String
    get() = fornecedor?.insc ?: ""
  override val horaSaida: String
    get() = "SEM VALOR"
  override val faturaDuplicata: String
    get() = "SEM VALOR"
  override val bcICMS: BigDecimal
    get() = BigDecimal(nota.baseIcmsProdutos)
  override val vlICMS: BigDecimal
    get() = BigDecimal(nota.valorIcmsProdutos)
  override val bcICMSSt: BigDecimal
    get() = BigDecimal(nota.baseIcmsSubstProduto)
  override val valorICMSSt: BigDecimal
    get() = BigDecimal(nota.icmsSubstProduto)
  override val vlProdutos: BigDecimal
    get() = BigDecimal(nota.valorTotalProduto)
  override val vlFrete: BigDecimal
    get() = BigDecimal(nota.valorFrete)
  override val vlSeguro: BigDecimal
    get() = BigDecimal(nota.valorSeguro)
  override val vlDesconto: BigDecimal
    get() = BigDecimal(nota.valorDesconto)
  override val vlOutrasDesp: BigDecimal
    get() = BigDecimal(nota.outrasDespesas)
  override val vlIPI: BigDecimal
    get() = BigDecimal(nota.valorIpiProdutos)
  override val vlTrib: BigDecimal
    get() = BigDecimal(999999)
  override val vlNota: BigDecimal
    get() = BigDecimal(nota.valorTotalNota)
  override val nomeTransportadora: String
    get() = nota.transportadora ?: ""
  override val fretePorConta: String
    get() = "SEM VALOR"
  override val codigoANTT: String
    get() = "SEM VALOR"
  override val placaVeiculo: String
    get() = "SEM VALOR"
  override val ufVeiculo: String
    get() = "SEM VALOR"
  override val cnpjCPF: String
    get() = transportadora?.cgc ?: ""
  override val enderecoTransportadora: String
    get() = transportadora?.addr ?: ""
  override val cidadeTransportadora: String
    get() = transportadora?.city ?: ""
  override val ufTransportadora: String
    get() = transportadora?.state ?: ""
  override val ieTransportadora: String
    get() = transportadora?.insc ?: ""
  override val quantidade: Long
    get() = nota.produtos.sumOf { it.quantDevolucao ?: 0 }.toLong()
  override val especie: String
    get() = "SEM VALOR"
  override val marca: String
    get() = "SEM VALOR"
  override val numeracao: String
    get() = "SEM VALOR"
  override val pesoBruto: BigDecimal
    get() = BigDecimal(999999)
  override val pesoLiquido: BigDecimal
    get() = BigDecimal(999999)
  override val codigo: String
    get() = produto.codigo?.toString() ?: ""
  override val descricao: String
    get() = produto.descricao ?: ""
  override val codigoBarras: String
    get() = produto.barcode
  override val ncm: String
    get() = produto.ncm ?: ""
  override val cst: String
    get() = produto.cst ?: ""
  override val cfop: String
    get() = produto.cfop ?: ""
  override val unidade: String
    get() = produto.un ?: ""
  override val quantProduto: BigDecimal
    get() = BigDecimal(produto.quantDevolucao ?: 0)
  override val valorUnitProduto: BigDecimal
    get() = BigDecimal(produto.valorUnit ?: 0.00)
  override val valorTotalProduto: BigDecimal
    get() = BigDecimal(produto.valorTotal ?: 0.00)
  override val bcICMSProduto: BigDecimal
    get() = BigDecimal(produto.baseIcmsDevolucao ?: 0.00)
  override val vlICMSProduto: BigDecimal
    get() = BigDecimal(produto.valIcmsDevolucao ?: 0.00)
  override val vlIPIProduto: BigDecimal
    get() = BigDecimal(produto.valIPIDevolucao ?: 0.00)
  override val aliqICMSProduto: BigDecimal
    get() = BigDecimal(produto.icms ?: 0.00)
  override val aliqIPIProduto: BigDecimal
    get() = BigDecimal(produto.ipi ?: 0.00)
  override val inscricaoMunicial: String
    get() = "SEM VALOR"
  override val valorServicos: BigDecimal
    get() = BigDecimal(0)
  override val bcISSQN: BigDecimal
    get() = BigDecimal(999999)
  override val valorISSQN: BigDecimal
    get() = BigDecimal(999999)
  override val informacoesComplementares: String
    get() = nota.observacaoDev ?: ""

}