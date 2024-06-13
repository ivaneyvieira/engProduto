package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.parserDate
import br.com.astrosoft.produto.model.estoque
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class Produtos(
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
      agrupar = false,
    )
    return ProdutoInventario.find(filtro).resumo()
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
