package br.com.astrosoft.produto.model

import br.com.astrosoft.devolucao.model.NotaEntradaVO
import br.com.astrosoft.framework.model.Config.appName
import br.com.astrosoft.framework.model.DB
import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.beans.*
import org.sql2o.Query

class QuerySaci : QueryDB(driver, url, username, password) {
  fun findUser(login: String?): UserSaci? {
    login ?: return null
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql, UserSaci::class) {
      addParameter("login", login)
      addParameter("appName", appName)
    }.firstOrNull()
  }

  fun findAllUser(): List<UserSaci> {
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql, UserSaci::class) {
      addParameter("login", "TODOS")
      addParameter("appName", appName)
    }
  }

  fun allLojas(): List<Loja> {
    val sql = "/sqlSaci/listLojas.sql"
    return query(sql, Loja::class)
  }

  fun findLojaByCnpj(cnpj: String): Loja? {
    val sql = "/sqlSaci/findLojaByCnpj.sql"
    return query(sql, Loja::class) {
      addOptionalParameter("cnpj", cnpj)
    }.firstOrNull()
  }

  fun findRetiraEntrega(filtro: FiltroProduto): List<ProdutoRetiraEntrega> {
    val sql = "/sqlSaci/findRetiraEntrega.sql"
    return query(sql, ProdutoRetiraEntrega::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("prdno", filtro.prdno)
      addOptionalParameter("typeno", filtro.typeno)
      addOptionalParameter("clno", filtro.clno)
      addOptionalParameter("vendno", filtro.vendno)
      addOptionalParameter("localizacao", filtro.localizacao)
      addOptionalParameter("nfno", filtro.nfno)
      addOptionalParameter("nfse", filtro.nfse)
      addOptionalParameter("isEdit", if (filtro.isEdit) "S" else "N")
    }
  }

  fun findProduto(filtro: FiltroProduto): List<Produto> {
    val sql = "/sqlSaci/findProdutos.sql"
    return query(sql, Produto::class) {
      addOptionalParameter("prdno", filtro.prdno)
      addOptionalParameter("typeno", filtro.typeno)
      addOptionalParameter("clno", filtro.clno)
      addOptionalParameter("vendno", filtro.vendno)
      addOptionalParameter("localizacao", filtro.localizacao)
    }
  }

  fun findProdutoReserva(filtro: FiltroProduto): List<ProdutoReserva> {
    val sql = "/sqlSaci/findPedidosReservados.sql"
    return query(sql, ProdutoReserva::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("prdno", filtro.prdno)
      addOptionalParameter("typeno", filtro.typeno)
      addOptionalParameter("clno", filtro.clno)
      addOptionalParameter("vendno", filtro.vendno)
      addOptionalParameter("localizacao", filtro.localizacao)
    }
  }

  fun findGrades(codigo: String): List<PrdGrade> {
    val sql = "/sqlSaci/findGrades.sql"
    return query(sql, PrdGrade::class) {
      addOptionalParameter("codigo", codigo)
    }
  }

  fun updateUser(user: UserSaci) {
    val sql = "/sqlSaci/updateUser.sql"
    script(sql) {
      addOptionalParameter("login", user.login)
      addOptionalParameter("bitAcesso", user.bitAcesso)
      addOptionalParameter("loja", user.storeno)
      addOptionalParameter("appName", appName)
    }
  }

  fun representante(vendno: Int): List<Representante> {
    val sql = "/sqlSaci/representantes.sql"
    return query(sql, Representante::class) {
      addOptionalParameter("vendno", vendno)
    }
  }

  fun listFuncionario(codigo: String): Funcionario? {
    val sql = "/sqlSaci/listFuncionario.sql"
    return query(sql, Funcionario::class) {
      addOptionalParameter("codigo", codigo.toIntOrNull() ?: 0)
    }.firstOrNull()
  }

  fun deleteFornecedorSap() {
    val sql = "/sqlSaci/deleteFornecedorSap.sql"
    script(sql)
  }

  fun saveNotaNdd(notas: List<NotaEntradaVO>) {
    val sql = "/sqlSaci/saveNotaNdd.sql"
    script(sql, notas.map { nota ->
      { q: Query ->
        q.addOptionalParameter("id", nota.id)
        q.addOptionalParameter("numero", nota.numero)
        q.addOptionalParameter("cancelado", nota.cancelado)
        q.addOptionalParameter("serie", nota.serie)
        q.addOptionalParameter("dataEmissao", nota.dataEmissao?.toSaciDate() ?: 0)
        q.addOptionalParameter("cnpjEmitente", nota.cnpjEmitente)
        q.addOptionalParameter("nomeFornecedor", nota.nomeFornecedor)
        q.addOptionalParameter("cnpjDestinatario", nota.cnpjDestinatario)
        q.addOptionalParameter("ieEmitente", nota.ieEmitente)
        q.addOptionalParameter("ieDestinatario", nota.ieDestinatario)
        q.addOptionalParameter("baseCalculoIcms", nota.valorNota ?: nota.baseCalculoIcms)
        q.addOptionalParameter("baseCalculoSt", nota.baseCalculoSt)
        q.addOptionalParameter("valorTotalProdutos", nota.valorTotalProdutos)
        q.addOptionalParameter("valorTotalIcms", nota.valorTotalIcms)
        q.addOptionalParameter("valorTotalSt", nota.valorTotalSt)
        q.addOptionalParameter("baseCalculoIssqn", nota.baseCalculoIssqn)
        q.addOptionalParameter("chave", nota.chave)
        q.addOptionalParameter("status", nota.status)
        q.addOptionalParameter("xmlAut", nota.xmlAut)
        q.addOptionalParameter("xmlCancelado", nota.xmlCancelado)
        q.addOptionalParameter("xmlNfe", nota.xmlNfe)
        q.addOptionalParameter("xmlDadosAdicionais", nota.xmlDadosAdicionais)
      }
    })
  }

  fun expira(pedido: Pedido) {
    val sql = "/sqlSaci/expiraPedido.sql"
    script(sql) {
      addOptionalParameter("loja", pedido.loja)
      addOptionalParameter("pedido", pedido.pedido)
    }
  }

  fun gravaGrade(entrega: ProdutoRetiraEntrega) {
    val sql = "/sqlSaci/salvaGrade.sql"
    script(sql) {
      addOptionalParameter("storeno", entrega.loja)
      addOptionalParameter("ordno", entrega.pedido)
      addOptionalParameter("codigo", entrega.codigo)
      addOptionalParameter("grade", entrega.grade)
      addOptionalParameter("gradeAlternativa", entrega.gradeAlternativa)
    }
  }

  fun findNotaSaida(filtro: FiltroNota) : List<NotaSaida>{
    val sql = "/sqlSaci/findNotaSaida.sql"
    val nfno = filtro.nfno
    val nfse = filtro.nfse
    return query(sql, NotaSaida::class) {
      addOptionalParameter("storeno", filtro.storeno)
      addOptionalParameter("nfno", nfno)
      addOptionalParameter("nfse", nfse)
    }
  }

  fun findProdutoNF(nfs : NotaSaida): List<ProdutoNF>{
    val sql = "/sqlSaci/findProdutosNFSaida.sql"
    return query(sql, ProdutoNF::class) {
      addOptionalParameter("storeno", nfs.loja)
      addOptionalParameter("pdvno", nfs.pdvno)
      addOptionalParameter("xano", nfs.xano)
    }
  }

  companion object {
    private val db = DB("saci")
    internal val driver = db.driver
    internal val url = db.url
    internal val username = db.username
    internal val password = db.password
    val ipServer: String? = url.split("/").getOrNull(2)
  }
}

val saci = QuerySaci()
