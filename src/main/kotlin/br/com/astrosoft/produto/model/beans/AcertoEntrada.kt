package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class AcertoEntrada(
  var loja: Int,
  var ni: Int,
  var notaFiscal: String?,
  var dataEmissao: LocalDate?,
  var fornecedor: Int?,
  var nomeFornecedor: String?,
  var observacao: String?,
  var valorNota: Double?,
  var codigoProduto: String?,
  var nomeProduto: String?,
  var rotulo: String?,
  var tributacao: String?,
  var grade: String?,
  var quantidade: Int?,
  var valorUnitario: Double?,
  var valorTotal: Double?,
) {
  companion object {
    fun findAll(filtro: FiltroAcertoEntrada): List<AcertoEntradaNota> {
      val lista = saci.findAcertoEstoqueEntrada(filtro)
      return lista.groupBy { it.notaFiscal }.mapNotNull { entry ->
        val nota = entry.value.firstOrNull() ?: return@mapNotNull null
        val produtos = entry.value.map { produto ->
          AcertoEntradaProduto(
            codigoProduto = produto.codigoProduto,
            nomeProduto = produto.nomeProduto,
            grade = produto.grade,
            quantidade = produto.quantidade,
            valorUnitario = produto.valorUnitario,
            valorTotal = produto.valorTotal,
            rotulo = produto.rotulo,
            tributacao = produto.tributacao,
          )
        }
        AcertoEntradaNota(
          loja = nota.loja,
          ni = nota.ni,
          notaFiscal = nota.notaFiscal,
          dataEmissao = nota.dataEmissao,
          fornecedor = nota.fornecedor,
          nomeFornecedor = nota.nomeFornecedor,
          observacao = nota.observacao,
          valor = nota.valorNota,
          produtos = produtos,
        )
      }
    }
  }
}

data class FiltroAcertoEntrada(
  val loja: Int,
  val query: String,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)

data class AcertoEntradaNota(
  val loja: Int,
  val ni: Int,
  val notaFiscal: String?,
  val dataEmissao: LocalDate?,
  val fornecedor: Int?,
  val nomeFornecedor: String?,
  val observacao: String?,
  val valor: Double?,
  val produtos: List<AcertoEntradaProduto>,
)

data class AcertoEntradaProduto(
  val codigoProduto: String?,
  val nomeProduto: String?,
  val grade: String?,
  val rotulo: String?,
  val tributacao: String?,
  val quantidade: Int?,
  val valorUnitario: Double?,
  val valorTotal: Double?,
)