package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaRecebimentoProduto(
  var loja: Int?,
  var data: LocalDate?,
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
  var prdno: String?,
  var codigo: String?,
  var barcodeStrList: String?,
  var descricao: String?,
  var grade: String?,
  var localizacao: String?,
  var quant: Int?,
  var estoque: Int?,
  var marca: Int?,
  var marcaSelecionada: Int?,
) {
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
)

enum class EMarcaRecebimento(val codigo: Int, val descricao: String) {
  TODOS(999, "Todos"),
  RECEBER(0, "Receber"),
  RECEBIDO(1, "Recebido")
}