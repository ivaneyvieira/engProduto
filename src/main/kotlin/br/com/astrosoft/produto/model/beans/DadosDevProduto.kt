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
  var dataVenda: String? = null
  var codCliente: Int? = null
  var nomeCliente: String? = null
  var prdno: String? = null
  var grade: String? = null
  var descricao: String? = null
  var unidade: String? = null
  var quantidadeDev: Int? = null
  var valorUnitario: Double? = null

  val codigo: Int?
    get() = prdno?.trim()?.toIntOrNull()

  val valorTotal: Double
    get() = (valorUnitario ?: 0.0) * (quantidadeDev ?: 0)
}

