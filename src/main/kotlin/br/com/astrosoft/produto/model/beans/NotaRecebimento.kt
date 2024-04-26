package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaRecebimento(
  var loja: Int?,
  var data: LocalDate?,
  var ni: Int?,
  var nfEntrada: String?,
  var custno: Int?,
  var vendno: Int?,
  var fornecedor: String?,
  var valorNF: Double?,
  var pedComp: Int?,
  var transp: Int?,
  var cte: String?,
  var volume: Int?,
  var peso: Int?,
  val produtos: List<NotaRecebimentoProduto>,
) {

  companion object {
    fun findAll(filtro: FiltroNotaRecebimentoProduto): List<NotaRecebimento> {
      return saci.findNotaRecebimentoProduto(filtro).groupBy { it.ni }.mapNotNull { entry ->
        val produtos = entry.value
        val nota = produtos.firstOrNull()
        nota?.let {
          NotaRecebimento(
            loja = nota.loja,
            data = nota.data,
            ni = nota.ni,
            nfEntrada = nota.nfEntrada,
            custno = nota.custno,
            vendno = nota.vendno,
            fornecedor = nota.fornecedor,
            valorNF = nota.valorNF,
            pedComp = nota.pedComp,
            transp = nota.transp,
            cte = nota.cte,
            volume = nota.volume,
            peso = nota.peso,
            produtos = produtos
          )
        }
      }
    }
  }
}