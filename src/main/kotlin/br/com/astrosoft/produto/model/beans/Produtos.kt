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
  var qtty01: Int?,
  var venc01: String?,
  var qtty02: Int?,
  var venc02: String?,
  var qtty03: Int?,
  var venc03: String?,
  var qtty04: Int?,
  var venc04: String?,
) {
  val qttyInv: Int?
    get() = (qtty01 ?: 0).plus(qtty02 ?: 0).plus(qtty03 ?: 0).plus(qtty04 ?: 0)

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

  companion object {
    fun find(filter: FiltroListaProduto, withSaldoApp: Boolean): List<Produtos> {
      val lista = saci.listaProdutos(filter)
      if (withSaldoApp) {
        val saldoApp = estoque.consultaSaldo(filter.temGrade).groupBy {
          PrdGradeList(it.codigo, it.grade)
        }
        lista.forEach { prd ->
          prd.MF_App = saldoApp[PrdGradeList(prd.codigo, prd.grade ?: "")]?.sumOf { it.saldo }
        }
      }
      return lista
    }

    fun findLoja(filter: FiltroListaProduto, withSaldoApp: Boolean): List<Produtos> {
      val qtdList = if (filter.loja == 0)
        saci.qtdVencimento()
      else emptyList()
      val lista = find(filter, withSaldoApp)
      return lista.flatMap { prd ->
        listOf(
          prd.copy(2, prd.DS_TT ?: 0).let {
            if (filter.loja == 0)
              it.setQtd(qtdList)
            else it
          },
          prd.copy(3, prd.MR_TT ?: 0).let {
            if (filter.loja == 0)
              it.setQtd(qtdList)
            else it
          },
          prd.copy(4, prd.MF_TT ?: 0).let {
            if (filter.loja == 0)
              it.setQtd(qtdList)
            else it
          },
          prd.copy(5, prd.PK_TT ?: 0).let {
            if (filter.loja == 0)
              it.setQtd(qtdList)
            else it
          },
          prd.copy(8, prd.TM_TT ?: 0).let {
            if (filter.loja == 0) it.setQtd(qtdList) else it
          },
        ).filter { it.storeno == filter.loja || filter.loja == 0 }
      }.sortedWith(compareBy({ it.codigo }, { it.grade }, { it.storeno }))
    }

    private fun Produtos.setQtd(qtdList: List<QtdVencimento>): Produtos {
      val qtdNum = qtdList.filter { it.prdno == prdno && it.grade == grade && it.storeno == storeno }
      val qtd01 = qtdNum.firstOrNull { it.num == 1 }
      val qtd02 = qtdNum.firstOrNull { it.num == 2 }
      val qtd03 = qtdNum.firstOrNull { it.num == 3 }
      val qtd04 = qtdNum.firstOrNull { it.num == 4 }

      return this.apply {
        qtty01 = qtd01?.quantidade
        venc01 = qtd01?.vencimento
        qtty02 = qtd02?.quantidade
        venc02 = qtd02?.vencimento
        qtty03 = qtd03?.quantidade
        venc03 = qtd03?.vencimento
        qtty04 = qtd04?.quantidade
        venc04 = qtd04?.vencimento
      }
    }
  }

  val siglaLoja: String
    get() = when (storeno) {
      2    -> "DS"
      3    -> "MR"
      4    -> "MF"
      5    -> "PK"
      8    -> "TM"
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
      venc01 = venc01,
      qtty02 = qtty02,
      venc02 = venc02,
      qtty03 = qtty03,
      venc03 = venc03,
      qtty04 = qtty04,
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
