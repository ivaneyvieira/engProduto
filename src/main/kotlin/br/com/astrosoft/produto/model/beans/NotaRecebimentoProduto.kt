package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaRecebimentoProduto(
  var loja: Int?,
  var lojaSigla: String?,
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
  var transportadora: String?,
  var cte: Int?,
  var volume: Int?,
  var peso: Double?,
  var usernoRecebe: Int?,
  var usuarioRecebe: String?,
  var prdno: String?,
  var codigo: String?,
  var vendnoProduto: Int?,
  var barcodeStrList: String?,
  var barcodeStrListEntrada: String?,
  var descricao: String?,
  var grade: String?,
  var localizacao: String?,
  var localizacaoSaci: String?,
  var quant: Int?,
  var estoque: Int?,
  var marca: Int?,
  var refFabrica: String?,
  var cfop: String?,
  var cst: String?,
  var un: String?,
  var marcaSelecionada: Int?,
  var validadeValida: String?,
  var validade: Int?,
  var vencimento: LocalDate?,
  var tipoValidade: String?,
  var tempoValidade: Int?,
  var observacaoNota: String?,
  var quantFile: Int?,
  var tipoNota: String?,
  var selecionado: Boolean? = false,
  var dataVenda: LocalDate?,
  var vendas: Int?,
  var qtty01: Int?,
  var venc01: String?,
  var qtty02: Int?,
  var venc02: String?,
  var qtty03: Int?,
  var venc03: String?,
  var qtty04: Int?,
  var venc04: String?,
  var valorUnit: Double?,
  var valorTotal: Double?,
  var valorDesconto: Double?,
  var baseIcms: Double?,
  var valIcms: Double?,
  var valIPI: Double?,
  var icms: Double?,
  var ipi: Double?,
  var frete: Double?,
  var outDesp: Double?,
  var icmsSubst: Double?,
) {
  val totalGeral
    get() = (valorTotal ?: 0.00) + (frete ?: 0.00) + (outDesp ?: 0.00) + (valIPI ?: 0.00) +
            (icmsSubst ?: 0.00) - (valorDesconto ?: 0.00)

  val localizacaoSaciStr: String
    get() = "       ${localizacaoSaci ?: ""}"
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

  fun recebe(user: UserSaci) {
    this.usernoRecebe = user.no
    this.marcaEnum = EMarcaRecebimento.RECEBIDO
    salva()
  }

  fun devolver() {
    this.usernoRecebe = 0
    this.marcaEnum = EMarcaRecebimento.RECEBER
    salva()
  }

  fun salvaVencimento() {
    saci.updateProduto(this)
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
  val grade: String = "",
  val tipoNota: EListaContas,
  val temAnexo: ETemAnexo = ETemAnexo.TODOS,
)

enum class ETemAnexo(val codigo: String, val descricao: String) {
  TEM_ANEXO(codigo = "S", "Sim"),
  SEM_ANEXO(codigo = "N", "Não"),
  TODOS(codigo = "T", "Todos"),
}

enum class EListaContas(val codigo: String, val descricao: String) {
  RECEBIMENTO(codigo = "R", "Recebimento"),
  DEVOLUCAO(codigo = "D", "Devolução"),
  TRANSFERENCIA(codigo = "X", "Transferência"),
  RECLASSIFICA(codigo = "C", "Reclassificação"),
  TODOS(codigo = "T", "Todos"),
}

enum class EMarcaRecebimento(val codigo: Int, val descricao: String) {
  TODOS(999, "Todos"),
  RECEBER(0, "Receber"),
  RECEBIDO(1, "Recebido")
}