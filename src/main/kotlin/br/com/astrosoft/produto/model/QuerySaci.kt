package br.com.astrosoft.produto.model

import br.com.astrosoft.devolucao.model.NotaEntradaVO
import br.com.astrosoft.framework.model.Config.appName
import br.com.astrosoft.framework.model.DB
import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.model.SqlLazy
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

  fun findLocais(): List<Local> {
    val sql = "/sqlSaci/findLocais.sql"
    return query(sql, Local::class)
  }

  fun updateUser(user: UserSaci) {
    val sql = "/sqlSaci/updateUser.sql"
    script(sql) {
      addOptionalParameter("login", user.login)
      addOptionalParameter("bitAcesso", user.bitAcesso)
      addOptionalParameter("loja", user.storeno)
      addOptionalParameter("appName", appName)
      addOptionalParameter("locais", user.locais)
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

  fun statusPedido(pedido: Pedido, status: EStatusPedido) {
    val sql = "/sqlSaci/expiraPedido.sql"
    script(sql) {
      addOptionalParameter("loja", pedido.loja)
      addOptionalParameter("pedido", pedido.pedido)
      addOptionalParameter("status", status.codigo)
    }
  }

  fun statusPedido(pedido: ProdutoPedidoVenda, status: EStatusPedido) {
    val sql = "/sqlSaci/expiraPedidoProduto.sql"
    script(sql) {
      addOptionalParameter("loja", pedido.loja)
      addOptionalParameter("pedido", pedido.ordno)
      addOptionalParameter("codigo", pedido.codigo)
      addOptionalParameter("grade", pedido.grade)
      addOptionalParameter("status", status.codigo)
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

  fun salvaProdutosNFS(podutoNF: ProdutoNFS) {
    val sql = "/sqlSaci/salvaProdutosNFS.sql"
    script(sql) {
      addOptionalParameter("storeno", podutoNF.loja)
      addOptionalParameter("pdvno", podutoNF.pdvno)
      addOptionalParameter("xano", podutoNF.xano)
      addOptionalParameter("codigo", podutoNF.codigo)
      addOptionalParameter("grade", podutoNF.grade)
      addOptionalParameter("gradeAlternativa", podutoNF.gradeAlternativa)
      addOptionalParameter("marca", podutoNF.marca)
      addOptionalParameter("usuarioCD", podutoNF.usuarioCD)
      addOptionalParameter("usuarioExp", podutoNF.usuarioExp)
    }
  }

  fun salvaProdutosPedidoVenda(podutoPedidoVenda: ProdutoPedidoVenda) {
    val sql = "/sqlSaci/salvaProdutosPedidoVenda.sql"
    script(sql) {
      addOptionalParameter("storeno", podutoPedidoVenda.loja)
      addOptionalParameter("ordno", podutoPedidoVenda.ordno)
      addOptionalParameter("codigo", podutoPedidoVenda.codigo)
      addOptionalParameter("grade", podutoPedidoVenda.grade)
      addOptionalParameter("gradeAlternativa", podutoPedidoVenda.gradeAlternativa)
      addOptionalParameter("marca", podutoPedidoVenda.marca)
      addOptionalParameter("usuarioCD", podutoPedidoVenda.usuarioCD)
    }
  }

  fun salvaProdutosRessuprimento(podutoPedidoVenda: ProdutoRessuprimento) {
    val sql = "/sqlSaci/salvaProdutosRessuprimento.sql"
    script(sql) {
      addOptionalParameter("ordno", podutoPedidoVenda.ordno)
      addOptionalParameter("codigo", podutoPedidoVenda.codigo)
      addOptionalParameter("grade", podutoPedidoVenda.grade)
      addOptionalParameter("marca", podutoPedidoVenda.marca)
      addOptionalParameter("usuarioCD", podutoPedidoVenda.usuarioCD)
    }
  }

  fun findNotaSaida(filtro: FiltroNota, locais: List<String>, sqlLazy: SqlLazy): List<NotaSaida> {
    val sql = "/sqlSaci/findNotaSaida.sql"
    val nfno = filtro.nfno
    val nfse = filtro.nfse
    return query(sql, NotaSaida::class, sqlLazy) {
      addOptionalParameter("marca", filtro.marca.num)
      addOptionalParameter("storeno", filtro.storeno)
      addOptionalParameter("nfno", nfno)
      addOptionalParameter("nfse", nfse)
      addOptionalParameter("cliente", filtro.cliente)
      addOptionalParameter("vendedor", filtro.vendedor)
      addOptionalParameter("locais", locais)
    }
  }

  fun findPedidoVenda(filtro: FiltroPedido, locais: List<String>): List<PedidoVenda> {
    val sql = "/sqlSaci/findPedidoVenda.sql"
    return query(sql, PedidoVenda::class) {
      addOptionalParameter("marca", filtro.marca.num)
      addOptionalParameter("storeno", filtro.storeno)
      addOptionalParameter("ordno", filtro.ordno)
      addOptionalParameter("locais", locais)
    }
  }

  fun findRessuprimento(filtro: FiltroRessuprimento, locais: List<String>): List<Ressuprimento> {
    val sql = "/sqlSaci/findRessuprimento.sql"
    return query(sql, Ressuprimento::class) {
      addOptionalParameter("marca", filtro.marca.num)
      addOptionalParameter("ordno", filtro.numero)
      addOptionalParameter("locais", locais)
    }
  }

  fun findProdutoNF(nfs: NotaSaida, marca: EMarcaNota, locais: List<String>): List<ProdutoNFS> {
    val sql = "/sqlSaci/findProdutosNFSaida.sql"
    return query(sql, ProdutoNFS::class) {
      addOptionalParameter("storeno", nfs.loja)
      addOptionalParameter("pdvno", nfs.pdvno)
      addOptionalParameter("xano", nfs.xano)
      addOptionalParameter("marca", marca.num)
      addOptionalParameter("locais", locais)
    }
  }

  fun findProdutoPedidoVenda(pedido: PedidoVenda, marca: EMarcaPedido, locais: List<String>): List<ProdutoPedidoVenda> {
    val sql = "/sqlSaci/findProdutosPedidoVenda.sql"
    return query(sql, ProdutoPedidoVenda::class) {
      addOptionalParameter("storeno", pedido.loja)
      addOptionalParameter("ordno", pedido.ordno)
      addOptionalParameter("marca", marca.num)
      addOptionalParameter("locais", locais)
    }
  }

  fun findProdutoRessuprimento(pedido: Ressuprimento,
                               marca: EMarcaRessuprimento,
                               locais: List<String>): List<ProdutoRessuprimento> {
    val sql = "/sqlSaci/findProdutosRessuprimento.sql"
    return query(sql, ProdutoRessuprimento::class) {
      addOptionalParameter("ordno", pedido.numero)
      addOptionalParameter("marca", marca.num)
      addOptionalParameter("locais", locais)
    }
  }

  fun findNotaEntrada(filtro: FiltroNotaEntrada): List<NotaEntrada> {
    val sql = "/sqlSaci/findNotaEntrada.sql"
    return query(sql, NotaEntrada::class) {
      addOptionalParameter("chave", filtro.chave)
    }
  }

  fun marcaNotaEntrada(chave: String): NotaEntrada? {
    val sql = "/sqlSaci/marcaNotaEntrada.sql"
    script(sql) {
      addOptionalParameter("chave", chave)
    }
    return findNotaEntrada(FiltroNotaEntrada(chave)).firstOrNull()
  }

  fun findProdutoNFE(nota: NotaEntrada, marca: Boolean): List<ProdutoNFE> {
    val sql = "/sqlSaci/findProdutosNFEntrada.sql"
    return query(sql, ProdutoNFE::class) {
      addOptionalParameter("ni", nota.ni)
      addOptionalParameter("marca", if (marca) 1 else 0)
    }
  }

  fun findProdutoNFEConf(nota: NotaEntrada): List<ProdutoNFE> {
    val sql = "/sqlSaci/findProdutosNFEntradaConf.sql"
    return query(sql, ProdutoNFE::class) {
      addOptionalParameter("ni", nota.ni)
    }
  }

  fun addProdutoConf(nota : NotaEntrada, barcode: String, quant : Int){
    val sql = "/sqlSaci/addProdutoConf.sql"
    return script(sql) {
      addOptionalParameter("ni", nota.ni)
      addOptionalParameter("barcode", barcode)
      addOptionalParameter("quant", quant)
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
