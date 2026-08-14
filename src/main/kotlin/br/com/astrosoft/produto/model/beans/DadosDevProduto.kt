package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

class DadosDevProduto {
  var ni: Int? = null
  var loja: Int? = null
  var nfdno: String? = null
  var nfdse: String? = null
  var dataDevolucao: LocalDate? = null
  var valorDev: Double? = null
  var obs: String? = null
  var nfVenda: String? = null
  var obsTipo: String? = null
  var dataVenda: LocalDate? = null
  var codCliente: Int? = null
  var nomeCliente: String? = null
  var prdno: String? = null
  var grade: String? = null
  var descricao: String? = null
  var unidade: String? = null
  var quantidadeDev: Int? = null
  var valorUnitario: Double? = null
  var nfno: Int? = null
  var nfse: String? = null
  var pdvno: Int? = null
  var xano: Int? = null
  var nfTipo: Int? = null
  var userSolicitacao: Int? = null
  var loginSolicitacao: String? = null
  var nomeSolicitacao: String? = null
  var userTroca: Int? = null
  var loginTroca: String? = null
  var nomeTroca: String? = null
  var produtoTroca: String? = null
  var tipoDev: String? = null
  var nfEntRet: Int? = null
  var produtoTrocaItem: String? = null
  var quantidadeTipo: Int? = null

  val codigo: Int?
    get() = prdno?.trim()?.toIntOrNull()

  val valorTotal: Double
    get() = (valorUnitario ?: 0.0) * (quantidadeDev ?: 0)
}

