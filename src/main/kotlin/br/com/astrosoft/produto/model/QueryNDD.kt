package br.com.astrosoft.produto.model

import br.com.astrosoft.framework.model.DB
import br.com.astrosoft.framework.model.DatabaseConfig
import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.produto.model.beans.NotaEntradaFileXML
import br.com.astrosoft.produto.nfeXml.ProdutoNotaEntradaVO

class QueryNDD : QueryDB(database) {

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

  fun listNFEntrada(chave: String): NotaEntradaFileXML? {
    val sql = "/sqlNDD/listNFEntrada.sql"
    return query(sql, NotaEntradaFileXML::class) {
      addOptionalParameter("chave", "NFe$chave")
    }.firstOrNull()
  }

  companion object {
    private val db = DB("nfd")

    val ipServer: String? = db.url.split("/").getOrNull(2)

    internal val database = DatabaseConfig(
      driver = db.driver,
      url = db.url,
      user = db.username,
      password = db.password
    )
  }
}

val ndd = QueryNDD()

