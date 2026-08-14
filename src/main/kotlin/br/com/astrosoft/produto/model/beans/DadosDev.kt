package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class DadosDev (
  var ni: Int? = null,
  var loja: Int? = null,
  var nfdno: String? = null,
  var nfdse: String? = null,
  var valorDev: Double? = null,
  var obs: String? = null,
  var nfVenda: String? = null,
  var obsTipo: String? = null,
  var dataVenda: LocalDate? = null,
  var codCliente: Int? = null,
  var nomeCliente: String? = null,
  val produtos: List<DadosDevProduto>,
){

  companion object {
    fun findAll(filtro: FiltroDadosDev): List<DadosDev> {
      return saci.findDadosDev(filtro).toDadosDev()
    }
  }
}

private fun List<DadosDevProduto>.toDadosDev(): List<DadosDev> {
  return this.groupBy { it.ni }.mapNotNull  {
    val lista = it.value
    val nota = lista.firstOrNull() ?: return@mapNotNull null
    DadosDev(
      ni = nota.ni,
      loja = nota.loja,
      nfdno = nota.nfdno,
      nfdse = nota.nfdse,
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