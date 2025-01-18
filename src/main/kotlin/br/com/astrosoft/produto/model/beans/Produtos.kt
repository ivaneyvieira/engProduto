package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.parserDate
import br.com.astrosoft.produto.model.estoque
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class Produtos(
  var storeno: Int?,
  var saldo: Int?,
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var grade: String?,
  var forn: Int?,
  var tributacao: String?,
  var abrev: String?,
  var tipo: Int?,
  var cl: Int?,
  var codBar: String?,
  var DS_TT: Int?,
  var MR_TT: Int?,
  var MF_TT: Int?,
  var PK_TT: Int?,
  var TM_TT: Int?,
  var estoque: Int?,
  var trib: String?,
  var refForn: String?,
  var pesoBruto: Double?,
  var uGar: String?,
  var tGar: Int?,
  var mesesGarantia: Int?,
  var emb: Double?,
  var ncm: String?,
  var site: String?,
  var unidade: String?,
  var foraLinha: String?,
  var ultVenda: LocalDate?,
  var ultCompra: LocalDate?,
  var qttyVendas: Int?,
  var qttyCompra: Int?,
  var MF_App: Int? = null,
  var localizacao: String?,
  var rotulo: String?,
  var mesesFabricacao: Int?,
  var entrada: Int?,
  var nfEntrada: String?,
  var dataEntrada: LocalDate?,
  var fabricacao: LocalDate?,
  var vencimento: LocalDate?,
  var vendas: Int? = null,
  var dataVenda: LocalDate? = null,
  var qtty01: Int?,
  var qttyDif01: Int?,
  var venc01: String?,
  var qtty02: Int?,
  var qttyDif02: Int?,
  var venc02: String?,
  var qtty03: Int?,
  var qttyDif03: Int?,
  var venc03: String?,
  var qtty04: Int?,
  var qttyDif04: Int?,
  var venc04: String?,
  var qttyInv: Int? = null,
) {
  fun produtosInventarioResumo(): List<ProdutoInventarioResumo> {
    val prdno = prdno ?: return emptyList()
    val grade = grade ?: return emptyList()
    val filtro = FiltroProdutoInventario(
      pesquisa = "",
      codigo = prdno.trim(),
      validade = 0,
      grade = grade,
      caracter = ECaracter.TODOS,
      mes = 0,
      ano = 0,
      storeno = 0,
    )
    return ProdutoInventario.find(filtro).resumo()
  }

  fun updateValidades(loja: Int) {
    saci.updateProduto(loja, this)
  }

  val MF_Dif
    get() = (MF_TT ?: 0) - (MF_App ?: 0)

  fun processaVendas() {
    saci.processaVendas(storeno ?: 0, prdno ?: "", grade ?: "")
  }

  companion object {
    fun find(filter: FiltroListaProduto, withSaldoApp: Boolean): List<Produtos> {
      val lista = saci.listaProdutos(filter)
      val qtdList = saci.qtdVencimento()
      if (withSaldoApp) {
        val saldoApp = estoque.consultaSaldo(filter.temGrade).groupBy {
          PrdGradeList(it.codigo, it.grade)
        }
        lista.forEach { prd ->
          prd.MF_App = saldoApp[PrdGradeList(prd.codigo, prd.grade ?: "")]?.sumOf { it.saldo }
        }
      }
      return lista.map { prd ->
        prd.storeno = filter.loja
        prd.setQtd(qtdList)
      }
    }

    fun findLoja(filter: FiltroListaProduto, withSaldoApp: Boolean): List<Produtos> {
      val qtdList = saci.qtdVencimento()
      val lista = find(filter, withSaldoApp)
      return lista.flatMap { prd ->
        listOf(
          prd.copy(2, prd.DS_TT ?: 0).setQtd(qtdList),
          prd.copy(3, prd.MR_TT ?: 0).setQtd(qtdList),
          prd.copy(4, prd.MF_TT ?: 0).setQtd(qtdList),
          prd.copy(5, prd.PK_TT ?: 0).setQtd(qtdList),
          prd.copy(8, prd.TM_TT ?: 0).setQtd(qtdList),
        ).filter { it.storeno == filter.loja || filter.loja == 0 }.            filter {
          when(filter.pesquisa){
            "DS" -> it.storeno == 2
            "MR" -> it.storeno == 3
            "MF" -> it.storeno == 4
            "PK" -> it.storeno == 5
            "TM" -> it.storeno == 6
            else -> true
          }
        }
      }.sortedWith(compareBy({ it.codigo }, { it.grade }, { it.storeno }))
    }

    private fun Produtos.setQtd(qtdList: List<QtdVencimento>): Produtos {
      val qtd = qtdList.firstOrNull { it.prdno == prdno && it.grade == grade && it.storeno == storeno }

      return this.apply {
        vendas = qtd?.vendas
        dataVenda = qtd?.dataVenda
        qttyInv = qtd?.qttyInv

        qttyDif01 = qtd?.qttyDif01
        qtty01 = qtd?.qtty01
        venc01 = qtd?.venc01
        qttyDif02 = qtd?.qttyDif02
        qtty02 = qtd?.qtty02
        venc02 = qtd?.venc02
        qttyDif03 = qtd?.qttyDif03
        qtty03 = qtd?.qtty03
        venc03 = qtd?.venc03
        qttyDif04 = qtd?.qttyDif04
        qtty04 = qtd?.qtty04
        venc04 = qtd?.venc04
      }
    }

    fun processaVendas() {
      saci.processaVendas()
    }
  }

  val siglaLoja: String
    get() = when (storeno) {
      2 -> "DS"
      3 -> "MR"
      4 -> "MF"
      5 -> "PK"
      8 -> "TM"
      else -> ""
    }

  fun copy(loja: Int, saldo: Int): Produtos {
    return Produtos(
      storeno = loja,
      saldo = saldo,
      prdno = prdno,
      codigo = codigo,
      descricao = descricao,
      grade = grade,
      forn = forn,
      tributacao = tributacao,
      abrev = abrev,
      tipo = tipo,
      cl = cl,
      codBar = codBar,
      DS_TT = DS_TT,
      MR_TT = MR_TT,
      MF_TT = MF_TT,
      PK_TT = PK_TT,
      TM_TT = TM_TT,
      estoque = estoque,
      trib = trib,
      refForn = refForn,
      pesoBruto = pesoBruto,
      uGar = uGar,
      tGar = tGar,
      mesesGarantia = mesesGarantia,
      emb = emb,
      ncm = ncm,
      site = site,
      unidade = unidade,
      foraLinha = foraLinha,
      ultVenda = ultVenda,
      ultCompra = ultCompra,
      qttyVendas = qttyVendas,
      qttyCompra = qttyCompra,
      MF_App = MF_App,
      localizacao = localizacao,
      rotulo = rotulo,
      mesesFabricacao = mesesFabricacao,
      entrada = entrada,
      nfEntrada = nfEntrada,
      dataEntrada = dataEntrada,
      fabricacao = fabricacao,
      vencimento = vencimento,
      qtty01 = qtty01,
      qttyDif01 = qttyDif01,
      venc01 = venc01,
      qtty02 = qtty02,
      qttyDif02 = qttyDif02,
      venc02 = venc02,
      qtty03 = qtty03,
      qttyDif03 = qttyDif03,
      venc03 = venc03,
      qtty04 = qtty04,
      qttyDif04 = qttyDif04,
      venc04 = venc04,
    )
  }
}

data class PrdGradeList(val codigo: Int?, val grade: String)

data class FiltroListaProduto(
  val pesquisa: String,
  val marcaPonto: EMarcaPonto,
  val inativo: EInativo,
  val codigo: Int,
  val listVend: List<Int>,
  val tributacao: String,
  val typeno: Int,
  val clno: Int,
  val diVenda: LocalDate?,
  val dfVenda: LocalDate?,
  val diCompra: LocalDate?,
  val dfCompra: LocalDate?,
  val temGrade: Boolean,
  val grade: String,
  val loja: Int,
  val estoque: EEstoqueList,
  val saldo: Int,
  val validade: Int,
  val temValidade: Boolean,
) {
  val pesquisaNumero: Int?
    get() = pesquisa.toIntOrNull()

  val pesquisaData: LocalDate?
    get() = pesquisa.parserDate()

  val pesquisaString: String?
    get() = if (pesquisa.matches("^[0-9]$".toRegex()) == true) null else pesquisa
}

enum class EMarcaPonto(val codigo: String?, val descricao: String) {
  NAO("N", "Não"), SIM("S", "Sim"), TODOS("T", "Todos")
}

enum class EInativo(val codigo: String?, val descricao: String) {
  NAO("N", "Não"), SIM("S", "Sim"), TODOS("T", "Todos")
}

enum class EEstoqueTotal(val codigo: String?, val descricao: String) {
  MENOR("<", "<"), MAIOR(">", ">"), IGUAL("=", "="), TODOS("T", "Todos")
}

enum class EEstoqueList(val codigo: String?, val descricao: String) {
  MENOR("<", "<"), MAIOR(">", ">"), IGUAL("=", "="), TODOS("T", "Todos")
}
