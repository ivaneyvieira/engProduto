package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class AcertoSaida(
  var loja: Int,
  var notaFiscal: String?,
  var dataEmissao: LocalDate?,
  var cliente: Int?,
  var nomeCliente: String?,
  var observacao: String?,
  var codigoProduto: String?,
  var nomeProduto: String?,
  var grade: String?,
  var quantidade: Int?,
  var valorUnitario: Double?,
  var valorTotal: Double?,
) {
  companion object {
    fun findAll(filtro: FiltroAcertoSaida): List<AcertoSaidaNota> {
      val lista = saci.findAcertoEstoqueSaida(filtro)
      return lista.groupBy { it.notaFiscal }.mapNotNull { entry ->
        val nota = entry.value.firstOrNull() ?: return@mapNotNull null
        val produtos = entry.value.map { produto ->
          AcertoSaidaProduto(
            codigoProduto = produto.codigoProduto,
            nomeProduto = produto.nomeProduto,
            grade = produto.grade,
            quantidade = produto.quantidade,
            valorUnitario = produto.valorUnitario,
            valorTotal = produto.valorTotal,
          )
        }
        AcertoSaidaNota(
          loja = nota.loja,
          notaFiscal = nota.notaFiscal,
          dataEmissao = nota.dataEmissao,
          cliente = nota.cliente,
          nomeCliente = nota.nomeCliente,
          observacao = nota.observacao,
          produtos = produtos,
        )
      }
    }
  }
}

data class FiltroAcertoSaida(
  val loja: Int,
  val query: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)

data class AcertoSaidaNota(
  val loja: Int,
  val notaFiscal: String?,
  val dataEmissao: LocalDate?,
  val cliente: Int?,
  val nomeCliente: String?,
  val observacao: String?,
  val produtos: List<AcertoSaidaProduto>,
)

data class AcertoSaidaProduto(
  val codigoProduto: String?,
  val nomeProduto: String?,
  val grade: String?,
  val quantidade: Int?,
  val valorUnitario: Double?,
  val valorTotal: Double?,
)