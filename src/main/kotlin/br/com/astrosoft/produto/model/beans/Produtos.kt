package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.parserDate
import br.com.astrosoft.produto.model.estoque
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class Produtos(
  val prdno: String,
  val codigo: Int,
  val descricao: String,
  val grade: String,
  val forn: Int,
  val tributacao: String?,
  val abrev: String,
  val tipo: Int,
  val cl: Int,
  val codBar: String,
  val DS_VA: Int,
  val DS_AT: Int,
  val DS_TT: Int,
  val MR_VA: Int,
  val MR_AT: Int,
  val MR_TT: Int,
  val MF_VA: Int,
  val MF_AT: Int,
  val MF_TT: Int,
  val PK_VA: Int,
  val PK_AT: Int,
  val PK_TT: Int,
  val TM_VA: Int,
  val TM_AT: Int,
  val TM_TT: Int,
  val estoque: Int,
  val qtPedido: Int,
  val trib: String,
  val refForn: String,
  val pesoBruto: Double,
  val uGar: String,
  val tGar: Int,
  val emb: Double,
  val ncm: String,
  val site: String,
  val unidade: String,
  val foraLinha: String,
  val ultVenda: LocalDate?,
  val ultCompra: LocalDate?,
  val qttyVendas: Int?,
  val qttyCompra: Int?,
  var MF_App: Int? = null,
  val localizacao: String?,
  val rotulo: String?,
) {
  val MF_Dif
    get() = MF_TT - (MF_App ?: 0)

  companion object {
    fun find(filter: FiltroListaProduto, withSaldoApp: Boolean): List<Produtos> {
      val lista = saci.listaProdutos(filter)
      if (withSaldoApp) {
        val saldoApp = estoque.consultaSaldo(filter.temGrade).groupBy {
          PrdGradeList(it.codigo, it.grade)
        }
        lista.forEach { prd ->
          prd.MF_App = saldoApp[PrdGradeList(prd.codigo, prd.grade)]?.sumOf { it.saldo }
        }
      }
      return lista
    }
  }
}

data class PrdGradeList(val codigo: Int, val grade: String)

data class FiltroListaProduto(
  val pesquisa: String,
  val marcaPonto: EMarcaPonto,
  val todoEstoque: Boolean,
  val inativo: EInativo,
  val codigo: Int,
  val listVend: List<Int>,
  val tributacao: String,
  val typeno: Int,
  val clno: Int,
  val lojaEstoque: Int,
  val estoqueTotal: EEstoqueTotal,
  val diVenda: LocalDate?,
  val dfVenda: LocalDate?,
  val diCompra: LocalDate?,
  val dfCompra: LocalDate?,
  val temGrade: Boolean,
  val grade: String?,
  val loja: Int = 0,
  val estoque: EEstoqueList,
  val saldo: Int,
) {
  val pesquisaNumero: Int?
    get() = pesquisa.toIntOrNull()

  val pesquisaData: LocalDate?
    get() = pesquisa.parserDate()

  val pesquisaString: String?
    get() = if (pesquisa.matches("^[0-9]$".toRegex())) null else pesquisa
}

enum class EMarcaPonto(val codigo: String, val descricao: String) {
  NAO("N", "Não"), SIM("S", "Sim"), TODOS("T", "Todos")
}

enum class EInativo(val codigo: String, val descricao: String) {
  NAO("N", "Não"), SIM("S", "Sim"), TODOS("T", "Todos")
}

enum class EEstoqueTotal(val codigo: String, val descricao: String) {
  MENOR("<", "<"), MAIOR(">", ">"), IGUAL("=", "="), TODOS("T", "Todos")
}

enum class EEstoqueList(val codigo: String, val descricao: String) {
  MENOR("<", "<"), MAIOR(">", ">"), IGUAL("=", "="), TODOS("T", "Todos")
}
