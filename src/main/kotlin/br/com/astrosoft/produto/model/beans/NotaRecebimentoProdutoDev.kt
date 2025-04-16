package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class NotaRecebimentoProdutoDev(
  var loja: Int?,
  var lojaSigla: String?,
  var numeroDevolucao: Int?,
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
  var transportadora: String?,
  var cte: Int?,
  var dataDevolucao: LocalDate?,
  var volume: Int?,
  var peso: Double?,
  var usernoRecebe: Int?,
  var usuarioRecebe: String?,
  var prdno: String?,
  var codigo: Int?,
  var vendnoProduto: Int?,
  var barcodeStrList: String?,
  var barcodeStrListEntrada: String?,
  var descricao: String?,
  var grade: String?,
  var localizacao: String?,
  var localizacaoSaci: String?,
  var quant: Int?,
  var estoque: Int?,
  var refFabrica: String?,
  var cfop: String?,
  var cst: String?,
  var un: String?,
  var validadeValida: String?,
  var validade: Int?,
  var tipoValidade: String?,
  var tempoValidade: Int?,
  var observacaoNota: String?,
  var tipoNota: String?,
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
  var obsGarantia: String?,
  var observacaoDev: String?,
  var dataColeta: LocalDate?
) {
  val chaveDevolucao: String
    get() {
      val motivo = tipoDevolucaoEnum
      return if (motivo?.notasMultiplas == true) {
        numeroDevolucao?.toString() ?: ""
      } else {
        "$loja-$ni-$tipoDevolucao-$numeroDevolucao"
      }
    }

  var situacaoDevEnum: EStituacaoDev
    get() = EStituacaoDev.entries.firstOrNull { it.num == situacaoDev } ?: EStituacaoDev.PENDENTE
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

  fun containBarcode(barcode: String): Boolean {
    return barcodeStrList?.split(",").orEmpty().map { it.trim() }.any { it == barcode }
  }

  fun salvaMotivoDevolucao() {
    saci.salvaMotivoDevolucao(this)
  }
}

data class FiltroNotaRecebimentoProdutoDev(
  val loja: Int,
  val pesquisa: String,
)


