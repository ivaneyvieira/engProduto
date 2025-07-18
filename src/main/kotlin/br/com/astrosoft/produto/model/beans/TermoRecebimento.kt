package br.com.astrosoft.produto.model.beans

import java.time.LocalDate
import kotlin.math.roundToInt

data class TermoRecebimento(
  val ni: Int,
  val dadosFornecedor: DadosTermoFornecedor,
  val dadosTransportadora: DadosTermoTransportadora,
  val dadosCliente: DadosTermoCliente,
  val nomeassinatura: String,
  val cpf: String,
  val empEmailTermo: String,
  val volumesInf: Int?,
  val volumesRec: Int?,
  val pesoBruto: Double?,
){
  fun volumesDivergencia(): Boolean {
    return (volumesInf ?: 0) != (volumesRec ?: 0)
  }
}

data class DadosTermoFornecedor(
  val cnpj: String,
  val nome: String,
  val endereco: String,
  val bairro: String,
  val cidade: String,
  val uf: String,
  val notaFiscal: String,
  val emissao: LocalDate?,
  val recebimento: LocalDate?,
  val valor: Double,
)

data class DadosTermoTransportadora(
  val cnpj: String,
  val nome: String,
  val endereco: String,
  val bairro: String,
  val cidade: String,
  val uf: String,
  val cte: String,
  val emissao: LocalDate?,
  val recebimento: LocalDate?,
  val valor: Double,
)

data class DadosTermoCliente(
  val cnpj: String,
  val nome: String,
  val endereco: String,
  val bairro: String,
  val cidade: String,
  val uf: String
)

fun List<NotaRecebimento>.termoRecebimento(): TermoRecebimento? {
  val dados = this.firstOrNull() ?: return null
  val fornecedor = DadosTermoFornecedor(
    cnpj = dados.cnpjFornecedor ?: "",
    nome = dados.fornecedor ?: "",
    endereco = dados.enderecoFornecedor ?: "",
    bairro = dados.bairroFornecedor ?: "",
    cidade = dados.cidadeFornecedor ?: "",
    uf = dados.ufFornecedor ?: "",
    notaFiscal = dados.nfEntrada ?: "",
    emissao = dados.emissao,
    recebimento = dados.data,
    valor = this.sumOf { it.valorNF ?: 0.00 },
  )
  val transportadora = DadosTermoTransportadora(
    cnpj = dados.cnpjTransportadora ?: "",
    nome = dados.transportadora ?: "",
    endereco = dados.enderecoTransportadora ?: "",
    bairro = dados.bairroTransportadora ?: "",
    cidade = dados.cidadeTransportadora ?: "",
    uf = dados.ufTransportadora ?: "",
    cte = dados.cte?.toString() ?: "",
    emissao = dados.emissao,
    recebimento = dados.data,
    valor = this.sumOf { it.valorNF ?: 0.00 },
  )
  val cliente = DadosTermoCliente(
    cnpj = dados.cnpjCliente ?: "",
    nome = dados.cliente ?: "",
    endereco = dados.enderecoCliente ?: "",
    bairro = dados.bairroCliente ?: "",
    cidade = dados.cidadeCliente ?: "",
    uf = dados.ufCliente ?: ""
  )

  return TermoRecebimento(
    ni = dados.ni ?: 0,
    dadosFornecedor = fornecedor,
    dadosTransportadora = transportadora,
    dadosCliente = cliente,
    nomeassinatura = dados.empNomeTermo ?: "",
    cpf = dados.empCpfTermo ?: "",
    empEmailTermo = "recebimentos@engecopi.com.br",
    pesoBruto = this.sumOf {
      it.pesoDevolucao ?: it.peso ?: 0.0
      if (((it.pesoDevolucao ?: 0.0) * 100).roundToInt() == 0) {
        it.peso ?: 0.0
      } else {
        it.pesoDevolucao ?: 0.0
      }
    },
    volumesInf = dados.volume,
    volumesRec = if ((dados.volumeDevolucao ?: 0) == 0) {
      dados.volume ?: 0
    } else {
      dados.volumeDevolucao ?: 0
    },
  )
}