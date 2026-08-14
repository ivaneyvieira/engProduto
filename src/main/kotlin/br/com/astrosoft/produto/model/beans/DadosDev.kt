package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class DadosDev(
  var ni: Int?,
  var loja: Int?,
  var nfdno: String?,
  var nfdse: String?,
  var dataDevolucao: LocalDate?,
  var valorDev: Double?,
  var obs: String?,
  var nfVenda: String?,
  var obsTipo: String?,
  var dataVenda: String?,
  var codCliente: Int?,
  var nomeCliente: String?,
  val produtos: List<DadosDevProduto>,
) {
  val nfDevolucao: String
    get() = "$nfdno/$nfdse"

  companion object {
    fun findAll(filtro: FiltroDadosDev): List<DadosDev> {
      return saci.findDadosDev(filtro).toDadosDev()
    }
  }
}

private fun List<DadosDevProduto>.toDadosDev(): List<DadosDev> {
  return this.groupBy { it.ni }.mapNotNull {
    val lista = it.value
    val nota = lista.firstOrNull() ?: return@mapNotNull null
    DadosDev(
      ni = nota.ni,
      loja = nota.loja,
      nfdno = nota.nfdno,
      nfdse = nota.nfdse,
      dataDevolucao = nota.dataDevolucao,
      valorDev = nota.valorDev,
      obs = nota.obs,
      nfVenda = nota.nfVenda,
      obsTipo = nota.obsTipo,
      dataVenda = nota.dataVenda,
      codCliente = nota.codCliente,
      nomeCliente = nota.nomeCliente,
      produtos = lista
    )
  }
}

data class FiltroDadosDev(
  val loja: Int,
  val pesquisa: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)