package br.com.astrosoft.produto.model

import br.com.astrosoft.framework.model.DB
import br.com.astrosoft.framework.model.DatabaseConfig
import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.produto.model.QuerySaci.Companion.database
import br.com.astrosoft.produto.model.beans.*

class QueryEstoque : QueryDB(database) {
  fun consultaValidadeEntrada(filtro: FiltroValidadeEntrada): List<ValidadeEntrada> {
    val sql = "/sqlEstoque/calculoValidade.sql"

    val listVend = filtro.listVend.joinToString(separator = ",")

    return query(sql, ValidadeEntrada::class) {
      addOptionalParameter("query", filtro.query)
      addOptionalParameter("marca", filtro.marca.codigo)

      addOptionalParameter("listVend", listVend)
      addOptionalParameter("tributacao", filtro.tributacao)
      addOptionalParameter("typeno", filtro.typeno)
      addOptionalParameter("clno", filtro.clno)
      addOptionalParameter("estoque", filtro.estoque.codigo)
      addOptionalParameter("nfe", filtro.nfe)
    }
  }

  fun consultaEstoqueApp(filtro: FiltroEstoqueApp): List<GarantiaEstoqueApp> {
    val sql = "/sqlEstoque/estoqueApp.sql"

    val listVend = filtro.listVend.joinToString(separator = ",")

    return query(sql, GarantiaEstoqueApp::class) {
      addOptionalParameter("query", filtro.query)
      addOptionalParameter("marca", filtro.marca.codigo)

      addOptionalParameter("listVend", listVend)
      addOptionalParameter("typeno", filtro.typeno)
      addOptionalParameter("clno", filtro.clno)
      addOptionalParameter("estoque", filtro.estoque.codigo)
      addOptionalParameter("nfe", filtro.nfe)
      addOptionalParameter("codigo", filtro.codigo ?: 0)
      addOptionalParameter("grade", filtro.grade)
      addOptionalParameter("temGrade", filtro.temGrade.let { if (it) "S" else "N" })
    }
  }

  fun consultaSaldo(grade: Boolean): List<SaldoApp> {
    val sql = "/sqlEstoque/saldoEstoque.sql"

    return query(sql, SaldoApp::class) {
      addOptionalParameter("grade", grade.let { if (it) "S" else "N" })
    }
  }

  companion object {
    private val db = DB("db")
    val ipServer: String? = db.url.split("/").getOrNull(2)

    internal val database = DatabaseConfig(
      driver = db.driver,
      url = db.url,
      user = db.username,
      password = db.password
    )
  }
}

val estoque = QueryEstoque()

