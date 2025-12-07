package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaVenda(
  var loja: Int?,
  var pdv: Int?,
  var transacao: Int?,
  var pedido: Int?,
  var data: LocalDate?,
  var nota: String?,
  var tipoNf: String?,
  var hora: LocalTime?,
  var tipoPgto: String?,
  var valor: Double?,
  var cliente: Int?,
  var uf: String?,
  var nomeCliente: String?,
  var vendedor: String?,
  var valorTipo: Double?,
  var obs: String?,
  var autoriza: String?,
  var solicitacaoTroca: String?,
  var produtoTroca: String?,
  var userTroca: Int?,
  var loginTroca: String?,
  var nameTroca: String?,
  //var userSolicitacao: Int?,
  //var loginSolicitacao: String?,
  var motivoTroca: String?,
  var motivoTrocaCod: String?,
  var nfEntRet: Int?,
  var notaEntrega: String?,
  var ni: Int?,
  var dataNi: LocalDate?,
  var pendente: String?,
) {
  var setMotivoTroca: Set<EMotivoTroca>
    get() = motivoTrocaCod?.split(";")?.mapNotNull { EMotivoTroca.find(it.trim()) }?.toSet().orEmpty()
    set(value) {
      motivoTrocaCod = value.joinToString(";") { it.codigo }
    }

  val strMotivoTroca: String
    get() = setMotivoTroca.sortedBy { it.codigo }.joinToString(", ") { it.descricao }

  var solicitacaoTrocaEnnum: ESolicitacaoTroca?
    get() = ESolicitacaoTroca.entries.firstOrNull { it.codigo == solicitacaoTroca }
    set(value) {
      solicitacaoTroca = value?.codigo
    }

  var produtoTrocaEnnum: EProdutoTroca?
    get() = EProdutoTroca.entries.firstOrNull { it.codigo == produtoTroca }
    set(value) {
      produtoTroca = value?.codigo
    }

  val solicitacaoTrocaDescricao: String
    get() = solicitacaoTrocaEnnum?.descricao ?: ""
  val produtoTrocaDescricao: String
    get() = produtoTrocaEnnum?.descricao ?: ""

  fun update() {
    saci.updateNotaVenda(this)
  }

  fun produtos(): List<ProdutoNFS> {
    val motivo = solicitacaoTrocaEnnum?.descricao
    return saci.findProdutoNF(this).map {
      it.motivo = motivo
      it
    }
  }

  fun notaDev(): List<EntradaDevCli> {
    val produtos = produtos()
    val produtosMap = produtos.mapNotNull { produto ->
      produto.ni ?: return@mapNotNull null
      val filtro = FiltroEntradaDevCli(
        loja = 0,
        query = produto.ni.toString(),
        dataI = produto.dataNi,
        dataF = produto.dataNi,
        dataLimiteInicial = null,
        impresso = null,
        tipo = ETipoDevCli.TODOS,
      )

      EntradaDevCli.findAll(filtro).firstOrNull {
        it.invno == produto.ni
      }
    }.distinctBy { it.invno }
    return produtosMap
  }

  fun motivo(): String? {
    val produtoTroca = when (produtoTrocaEnnum) {
      EProdutoTroca.Com   -> "P"
      EProdutoTroca.Sem   -> ""
      EProdutoTroca.Misto -> "M"
      null                -> return null
    }

    val solicitacaoTroca = when (solicitacaoTrocaEnnum) {
      ESolicitacaoTroca.Troca       -> "Troca"
      ESolicitacaoTroca.Estorno     -> "Estorno"
      ESolicitacaoTroca.Reembolso   -> "Reembolso"
      ESolicitacaoTroca.MudaCliente -> "Muda"
      null                          -> return null
    }

    return "$solicitacaoTroca $produtoTroca".trim()
  }

  fun salvaNfEntRet() {
    saci.salvaNfEntRet(this)
  }

  val numeroInterno: Int?
    get() {
      val regex = Regex("""NI[^0-9A-Z]*(\d+)""")
      val obsInput = obs?.uppercase() ?: return null
      val match = regex.find(obsInput) ?: return null
      val groups = match.groupValues
      return groups.getOrNull(1)?.toIntOrNull()
    }

  companion object {
    fun findAll(filtro: FiltroNotaVenda): List<NotaVenda> {
      return saci.findNotaVenda(filtro)
    }
  }
}

data class FiltroNotaVenda(
  val loja: Int,
  val pesquisa: String,
  val invno: Int = 0,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
)

enum class ESolicitacaoTroca(val codigo: String, val descricao: String) {
  Troca("T", "Troca"),
  Estorno("E", "Estorno"),
  Reembolso("R", "Reembolso"),
  MudaCliente("M", "Muda Cliente"),
}

enum class EProdutoTroca(val codigo: String, val descricao: String) {
  Com("C", "Com Produto"),
  Sem("S", "Sem Produto"),
  Misto("M", "Misto"),
}

enum class EMotivoTroca(val codigo: String, val descricao: String) {
  CompraErrada("CE", "Compra Errada"),
  VendaErrada("VE", "Venda Errada"),
  Desistencia("D", "Desistência"),
  MudaCliente("MC", "Muda Cliente"),
  MudaTipoNF("MT", "Muda Tipo NF"),
  MudaTipoVenda("MV", "Muda Tipo Venda"),
  ProdutoComDefeito("PD", "Produto com Defeito"),
  ProdutoSemEstoque("PE", "Produto sem Estoque");

  companion object {
    fun find(codigo: String): EMotivoTroca? {
      return EMotivoTroca.entries.firstOrNull { it.codigo == codigo }
    }
  }
}

enum class EDevolucaoStatus(val codigo: String, val descricao: String) {
  Vendas("V", "Vendas"),
  Pendente("P", "Pendente"),
  GeradaParcial("GP", "Parcial"),
  Gerada("G", "Total"),
  Todos("T", "Todos");
}

fun List<ProdutoNFS>.expande(): List<ProdutoNFS> {
  val grupo = this.groupBy { "${it.prdno} ${it.grade}" }
  val result = grupo.flatMap { entry ->
    val listPrd = entry.value
    val seqNI = listPrd.mapNotNull { it.ni }.distinct().sorted()

    val listPrdDev = listPrd.filter { it.devDB == true }.map {
      it.copy(
        quantidade = it.quantDev,
        total = ((it.quantDev ?: 0) * 1.00) * (it.preco ?: 0.00)
      )
    }.sortedBy { it.seq ?: 0 }
    val totalDev = listPrdDev.sumOf { it.quantDev ?: 0 }
    val quantidade = (listPrd.firstOrNull()?.quantidade ?: 0) - totalDev
    val total = (quantidade * 1.00) * (listPrd.firstOrNull()?.preco ?: 0.00)
    val prdCopy = listPrd.firstOrNull()?.copy(
      devDB = false,
      dev = false,
      ni = 0,
      dataNi = null,
      quantDev = quantidade,
      qtDevNI = null,
      temProduto = false,
      quantidade = quantidade,
      total = total,
      seq = null
    )
    buildList {
      listPrdDev.forEachIndexed { index, prd ->
        add(
          prd.copy(
            ni = seqNI.getOrNull(index)
          )
        )
      }
      if (prdCopy != null && (prdCopy.quantidade ?: 0) > 0) {
        add(prdCopy)
      }
    }
  }
  return result
}