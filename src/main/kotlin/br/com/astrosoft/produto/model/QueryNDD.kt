package br.com.astrosoft.produto.model

import br.com.astrosoft.devolucao.model.NotaEntradaVO
import br.com.astrosoft.produto.model.beans.ProdutoNotaEntradaVO
import br.com.astrosoft.framework.model.DB
import br.com.astrosoft.framework.model.QueryDB
import java.time.LocalDate

class QueryNDD : QueryDB(driver, url, username, password) {
  fun notasEntrada(): List<NotaEntradaVO> {
    val sql = "/sqlNDD/notasEntrada.sql"
    return query(sql, NotaEntradaVO::class) {
      addOptionalParameter("dataInicial", LocalDate.now().minusMonths(7))
    }
  }

  fun produtosNotasEntrada(id: Int): ProdutoNotaEntradaVO? {
    val sql = "/sqlNDD/produtosNotaEntrada.sql"
    return query(sql, ProdutoNotaEntradaVO::class) {
      addOptionalParameter("id", id)
    }.firstOrNull()
  }

  fun produtosNotasSaida(storeno: Int, numero: Int, serie: Int): ProdutoNotaEntradaVO? {
    val sql = "/sqlNDD/produtosNotaSaida.sql"
    return query(sql, ProdutoNotaEntradaVO::class) {
      addOptionalParameter("storeno", storeno)
      addOptionalParameter("numero", numero)
      addOptionalParameter("serie", serie)
    }.firstOrNull()
  }

  companion object {
    private val db = DB("ndd")
    internal val driver = db.driver
    internal val url = db.url
    internal val username = db.username
    internal val password = db.password
  }
}

val ndd = QueryNDD()

