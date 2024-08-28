package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaRecebimentoProduto(
  var loja: Int?,
  var data: LocalDate?,
  var login: String?,
  var emissao: LocalDate?,
  var ni: Int?,
  var nfEntrada: String?,
  var custno: Int?,
  var vendno: Int?,
  var fornecedor: String?,
  var valorNF: Double?,
  var pedComp: Int?,
  var transp: Int?,
  var cte: Int?,
  var volume: Int?,
  var peso: Double?,
  var usernoRecebe: Int?,
  var usuarioRecebe: String?,
  var prdno: String?,
  var codigo: String?,
  var vendnoProduto: Int?,
  var barcodeStrList: String?,
  var descricao: String?,
  var grade: String?,
  var localizacao: String?,
  var quant: Int?,
  var estoque: Int?,
  var marca: Int?,
  var marcaSelecionada: Int?,
  var validadeValida: String?,
  var validade: Int?,
  var vencimento: LocalDate?,
  var tipoValidade: String?,
  var tempoValidade: Int?,
) {
  val validadeStr
    get() = when (validade) {
      null -> ""
      999  -> "Indeterminada"
      else -> validade.toString()
    }
  val fabricacao: LocalDate?
    get() = vencimento?.minusMonths(validade?.toLong() ?: 0)?.withDayOfMonth(1)

  var marcaEnum: EMarcaRecebimento = EMarcaRecebimento.TODOS
    get() = EMarcaRecebimento.entries.firstOrNull { it.codigo == marca } ?: EMarcaRecebimento.TODOS
    set(value) {
      marca = value.codigo
      field = value
    }

  fun containBarcode(barcode: String): Boolean {
    return barcodeStrList?.split(",").orEmpty().map { it.trim() }.any { it == barcode }
  }

  fun salva() {
    saci.updateNotaRecebimentoProduto(this)
  }

  companion object {
    fun findAll(filtro: FiltroNotaRecebimentoProduto): List<NotaRecebimentoProduto> {
      return saci.findNotaRecebimentoProduto(filtro)
    }
  }
}

data class FiltroNotaRecebimentoProduto(
  val loja: Int,
  val pesquisa: String,
  val marca: EMarcaRecebimento,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val invno: Int = 0,
  val localizacao: List<String>,
  val prdno: String = "",
  val grade: String = "SEM GRADE",
  val listaContas: EListaContas = EListaContas.RECEBIMENTO
)

enum class EListaContas(val values: List<String>) {
  RECEBIMENTO(listOf("2.01.20", "2.01.21", "4.01.01.04.02", "6.03.01.01.01", "6.03.01.01.02")),
  DEVOLUCAO(listOf("2.01.25")),
  TODOS(values = RECEBIMENTO.values + DEVOLUCAO.values)
}

enum class EMarcaRecebimento(val codigo: Int, val descricao: String) {
  TODOS(999, "Todos"),
  RECEBER(0, "Receber"),
  RECEBIDO(1, "Recebido")
}