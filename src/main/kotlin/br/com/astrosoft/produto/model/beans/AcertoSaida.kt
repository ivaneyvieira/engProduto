package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class AcertoSaida (
  var loja: Int,
  var notaFiscal: String?,
  var dataEmissao: LocalDate?,
  var fornecedor: Int?,
  var nomeFornecedo: String?,
  var observacao: String?,
  var codigoProduto: String?,
  var nomeProduto: String?,
  var grade: String?,
  var quantidade: Int?,
  var valorUnitario: Double?,
  var valorTotal: Double?,
){
  companion object {
    fun findAll(filtro: FiltroAcertoSaida): List<AcertoSaida> {
      return saci.findAcertoEstoqueSaida(filtro)
    }
  }
}

data class FiltroAcertoSaida(
  val loja: Int,
  val query: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)