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
  var cliente: String?,
  var cnpjCliente: String?,
  var enderecoCliente: String?,
  var bairroCliente: String?,
  var cidadeCliente: String?,
  var ufCliente: String?,
  var vendno: Int?,
  var fornecedor: String?,
  var cnpjFornecedor: String?,
  var enderecoFornecedor: String?,
  var bairroFornecedor: String?,
  var cidadeFornecedor: String?,
  var ufFornecedor: String?,
  var valorNF: Double?,
  var pedComp: Int?,
  var transp: Int?,
  var transportadora: String?,
  var cnpjTransportadora: String?,
  var enderecoTransportadora: String?,
  var bairroTransportadora: String?,
  var cidadeTransportadora: String?,
  var ufTransportadora: String?,
  var cte: Int?,
  var dataDevolucao: LocalDate?,
  var volume: Int?,
  var peso: Double?,
  var usernoRecebe: Int?,
  var usuarioRecebe: String?,
  var usernoEnvio: Int?,
  var loginEnvio: String?,
  var usernoReceb: Int?,
  var loginReceb: String?,
  var empNoTermo: Int?,
  var empNomeTermo: String?,
  var empCpfTermo: String?,
  var empEmailTermo: String?,
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
  var motivoDevolucao: Int?,
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
    get() = "$loja-$ni-$motivoDevolucao-$notaDevolucao"

  var situacaoDevEnum: EStituacaoDev
    get() = EStituacaoDev.list().firstOrNull { it.num == situacaoDev } ?: EStituacaoDev.PEDIDO
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

  var motivoDevolucaoEnum: EMotivoDevolucao?
    get() = EMotivoDevolucao.findByNum(motivoDevolucao ?: 0)
    set(value) {
      motivoDevolucao = value?.num
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

  fun updateDevolucao(numero: Int, tipo: EMotivoDevolucao?) {
    tipo ?: return
    saci.saveMotivoDevolucao(this, tipo, numero)
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

enum class EMotivoDevolucao(
  val num: Int,
  val descricao: String,
  val notasMultiplas: Boolean,
  val fob: Boolean,
  val divergente: Boolean
) {
  AVARIA_TRANSPORTE(
    num = 1,
    descricao = "Avaria no Transporte",
    notasMultiplas = false,
    fob = false,
    divergente = false
  ),
  FALTA_TRANSPORTE(num = 2, descricao = "Falta no Transporte", notasMultiplas = false, fob = false, divergente = false),
  FALTA_FABRICA(num = 3, descricao = "Falta de Fabrica", notasMultiplas = false, fob = false, divergente = false),
  VALIDADE(num = 4, descricao = "Validade", notasMultiplas = false, false, divergente = false),
  DEFEITO_FABRICA(
    num = 7,
    descricao = "Defeito de Fabricação",
    notasMultiplas = true,
    fob = false,
    divergente = false
  ),
  SEM_IDENTIFICACAO(num = 5, descricao = "Sem Identificação", notasMultiplas = false, fob = false, divergente = false),
  EM_DESACORDO(num = 6, descricao = "Em Desacordo", notasMultiplas = false, fob = false, divergente = false),
  EM_GARANTIA(num = 8, descricao = "Garantia", notasMultiplas = true, fob = false, divergente = false),
  ACORDO_COMERCIAL(num = 13, descricao = "Acordo Comercial", notasMultiplas = true, fob = false, divergente = false),
  FRET_FOB(num = 9, descricao = "Frete FOB", notasMultiplas = false, fob = true, divergente = false),
  ASSISTENCIA(num = 10, descricao = "Assistência", notasMultiplas = false, fob = false, divergente = false),
  AJUSTE(num = 11, descricao = "Ajuste", notasMultiplas = false, fob = false, divergente = false),
  PRODUTO_TROCADO(num = 12, descricao = "Produto Trocado", notasMultiplas = false, fob = false, divergente = false),
  TROCA_CNPJ(num = 14, descricao = "Muda CNPJ", notasMultiplas = false, fob = false, divergente = false),
  DIVERGENTE(num = 15, descricao = "Divergente", notasMultiplas = false, fob = false, divergente = false);

  override fun toString(): String {
    return descricao
  }

  companion object {
    fun findByNum(num: Int): EMotivoDevolucao? {
      return entries.firstOrNull { it.num == num }
    }
  }
}

enum class EStituacaoDev(val num: Int, val descricao: String) {
  EDITOR(999, "Editor"),
  PEDIDO(0, "Pedido"),
  COLETA(9, "Coleta"),
  NFD(1, "NFD"),
  GARANTIA(6, "Garantia"),
  COLETAREP(13, "Coleta Rep"),
  TRANSPORTADORA(2, "Transportadora"),
  EMAIL(3, "E-mail"),
  RETORNO_NFD(num = 12, "Retorno NFD"),
  REPOSTO(4, "Reposto"),
  ACERTO(5, "Acerto"),
  ACERTO_PAGO(7, "Acerto Pago"),
  AJUSTE(8, "Ajuste"),
  DESCARTE(10, "Descarte"),
  NULO(11, "Nulo");

  companion object {
    fun list(): List<EStituacaoDev> {
      return entries.filter { it != EDITOR && it != NFD && it != GARANTIA }
    }

    fun findByNum(num: Int): EStituacaoDev? {
      return entries.firstOrNull { it.num == num }
    }
  }
}