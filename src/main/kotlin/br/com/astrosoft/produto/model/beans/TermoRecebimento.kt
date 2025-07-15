package br.com.astrosoft.produto.model.beans

data class TermoRecebimento(
  val dadosFornecedor: DadosTermoFornecedor,
  val dadosTransportadora: DadosTermoTransportadora,
  val dadosCliente: DadosTermoCliente,
)

data class DadosTermoFornecedor(
  val cnpj: String,
  val nome: String,
  val endereco: String,
  val bairro: String,
  val cidade: String,
  val uf: String
)

data class DadosTermoTransportadora(
  val cnpj: String,
  val nome: String,
  val endereco: String,
  val bairro: String,
  val cidade: String,
  val uf: String
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
    uf = dados.ufFornecedor ?: ""
  )
  val transportadora = DadosTermoTransportadora(
    cnpj = dados.cnpjTransportadora ?: "",
    nome = dados.transportadora ?: "",
    endereco = dados.enderecoTransportadora ?: "",
    bairro = dados.bairroTransportadora ?: "",
    cidade = dados.cidadeTransportadora ?: "",
    uf = dados.ufTransportadora ?: ""
  )
  val cliente = DadosTermoCliente(
    cnpj = dados.cliente ?: "",
    nome = dados.cnpjCliente ?: "",
    endereco = dados.enderecoCliente ?: "",
    bairro = dados.bairroCliente ?: "",
    cidade = dados.cidadeCliente ?: "",
    uf = dados.ufCliente ?: ""
  )

  return TermoRecebimento(fornecedor, transportadora, cliente)
}