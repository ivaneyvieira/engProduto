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
  var dataDevolucao: LocalDate?,
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
  var baseSubst: Double?,
  var tipoDevolucao: Int?,
  var quantDevolucao: Int?,
  var pesoDevolucao: Double?,
  var volumeDevolucao: Int?,
  var transpDevolucao: Int?,
  var transportadoraDevolucao: String?,
  var cteDevolucao: String?,
  var situacaoDev: Int?,
  var userDevolucao: String?,
  var notaDevolucao: String?,
  var emissaoDevolucao: LocalDate?,
  var valorDevolucao: Double?,
  var obsDevolucao: String?,
  var observacaoAdicional: String?,
) {
  val chaveNi
    get() = "$loja-$ni"
  val chaveDevolucao
    get() = "$loja-$ni-$tipoDevolucao-$notaDevolucao"

  var situacaoDevEnum: EStituacaoDev
    get() = EStituacaoDev.entries.firstOrNull { it.num == situacaoDev } ?: EStituacaoDev.PENDENCIA
    set(value) {
      situacaoDev = value.num
    }

  val valorTotalDevolucao
    get() = (valorUnit ?: 0.00) * ((quantDevolucao ?: 0) * 1.00)

  val valorDescontoDevolucao: Double?
    get() {
      if ((quant ?: 0) == 0) return null
      return (valorDesconto ?: 0.00) * (quantDevolucao ?: 0) / (quant ?: 1)
    }

  val freteDevolucao: Double?
    get() {
      if ((quant ?: 0) == 0) return null
      return (frete ?: 0.00) * (quantDevolucao ?: 0) / (quant ?: 1)
    }

  val outDespDevolucao: Double?
    get() {
      if ((quant ?: 0) == 0) return null
      return (outDesp ?: 0.00) * (quantDevolucao ?: 0) / (quant ?: 1)
    }

  val baseIcmsDevolucao: Double?
    get() {
      if ((quant ?: 0) == 0) return null
      return (baseIcms ?: 0.00) * (quantDevolucao ?: 0) / (quant ?: 1)
    }

  val icmsSubstDevolucao: Double?
    get() {
      if ((quant ?: 0) == 0) return null
      return (icmsSubst ?: 0.00) * (quantDevolucao ?: 0) / (quant ?: 1)
    }
  val valIcmsDevolucao: Double?
    get() {
      if ((quant ?: 0) == 0) return null
      return (valIcms ?: 0.00) * (quantDevolucao ?: 0) / (quant ?: 1)
    }
  val valIPIDevolucao: Double?
    get() {
      if ((quant ?: 0) == 0) return null
      return (valIPI ?: 0.00) * (quantDevolucao ?: 0) / (quant ?: 1)
    }

  val totalGeralDevolucao: Double
    get() {
      return (valorTotalDevolucao) + (freteDevolucao ?: 0.00) +
             (outDespDevolucao ?: 0.00) + (valIPIDevolucao ?: 0.00) +
             (icmsSubstDevolucao ?: 0.00) - (valorDescontoDevolucao ?: 0.00)
    }

  var tipoDevolucaoEnum: ETipoDevolucao?
    get() = ETipoDevolucao.findByNum(tipoDevolucao ?: 0)
    set(value) {
      tipoDevolucao = value?.num
    }

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

  fun updateDevolucao(numero: Int, tipo: ETipoDevolucao?) {
    tipo ?: return
    saci.saveTipoDevolucao(this, tipo, numero)
  }

  fun desfazerDevolucao() {
    saci.desfazerDevolucao(this)
  }

  fun devolucoes(): List<DevolucaoProduto> {
    return saci.findDevolucoes(this)
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

enum class ETipoDevolucao(val num: Int, val descricao: String, val notasMultiplas: Boolean, val fob: Boolean) {
  AVARIA_TRANSPORTE(1, "Avaria no Transporte", false, false),
  FALTA_TRANSPORTE(2, "Falta no Transporte", false, false),
  FALTA_FABRICA(3, "Falta de Fabrica", false, false),
  VENCIMENTO(4, "Vencimento", false, false),
  DEFEITO_FABRICA(7, "Defeito de Fabricação", false, false),
  SEM_IDENTIFICACAO(5, "Sem Identificação", false, false),
  EM_DESACORDO(6, "Em Desacordo Com Pedido", false, false),
  EM_GARANTIA(8, "Garantia", true, false),
  FRET_FOB(9, "Frete FOB", false, true),
  ASSISTENCIA(10, "Assistência", false, false),
  AJUSTE(11, "Ajuste", false, false),
  PRODUTO_TROCADO(12, "Produto Trocado", false, false);

  override fun toString(): String {
    return descricao
  }

  companion object {
    fun findByNum(num: Int): ETipoDevolucao? {
      return entries.firstOrNull { it.num == num }
    }
  }
}

enum class EStituacaoDev(val num: Int, val descricao: String) {
  PENDENCIA(0, "Pendencia"),
  COLETA(9, "Coleta"),
  NFD(1, "NFD"),
  GARANTIA(6, "Garantia"),
  TRANSPORTADORA(2, "Transportadora"),
  EMAIL(3, "E-mail"),
  REPOSTO(4, "Reposto"),
  ACERTO(5, "Acerto"),
  ACERTO_PAGO(7, "Acerto Pago"),
  AJUSTE(8, "Ajuste"),
}