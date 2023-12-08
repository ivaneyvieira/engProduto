package br.com.astrosoft.produto.model

import br.com.astrosoft.framework.model.DB
import br.com.astrosoft.framework.model.DatabaseConfig
import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.model.SqlLazy
import br.com.astrosoft.framework.model.config.AppConfig.appName
import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.beans.*
import org.sql2o.Query
import java.time.LocalDate

class QuerySaci : QueryDB(database) {
  fun findUser(login: String?): List<UserSaci> {
    login ?: return emptyList()
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql, UserSaci::class) {
      addParameter("login", login)
      addParameter("appName", appName)
    }
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

  fun findImpressoras(): List<Impressora> {
    val sql = "/sqlSaci/userPrint.sql"
    return query(sql, Impressora::class)
  }

  fun updateUser(user: UserSaci) {
    val sql = "/sqlSaci/updateUser.sql"
    script(sql) {
      addOptionalParameter("login", user.login)
      addOptionalParameter("bitAcesso", user.bitAcesso)
      addOptionalParameter("loja", user.storeno)
      addOptionalParameter("appName", appName)
      addOptionalParameter("locais", user.locais)
      addOptionalParameter("listaImpressora", user.listaImpressora)
      addOptionalParameter("listaLoja", user.listaLoja)
    }
  }

  fun representante(vendno: Int): List<Representante> {
    val sql = "/sqlSaci/representantes.sql"
    return query(sql, Representante::class) {
      addOptionalParameter("vendno", vendno)
    }
  }

  fun listFuncionario(codigo: Int): Funcionario? {
    val sql = "/sqlSaci/listFuncionario.sql"
    return query(sql, Funcionario::class) {
      addOptionalParameter("codigo", codigo)
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

  fun statusPedido(pedido: ProdutoPedidoTransf, status: EStatusPedido) {
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

  fun salvaProdutosPedidoTransf(podutoPedidoTransf: ProdutoPedidoTransf) {
    val sql = "/sqlSaci/salvaProdutosPedidoVenda.sql"
    script(sql) {
      addOptionalParameter("storeno", podutoPedidoTransf.loja)
      addOptionalParameter("ordno", podutoPedidoTransf.ordno)
      addOptionalParameter("codigo", podutoPedidoTransf.codigo)
      addOptionalParameter("grade", podutoPedidoTransf.grade)
      addOptionalParameter("gradeAlternativa", podutoPedidoTransf.gradeAlternativa)
      addOptionalParameter("marca", podutoPedidoTransf.marca)
      addOptionalParameter("usuarioCD", podutoPedidoTransf.usuarioCD)
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

  fun findNotaSaida(filtro: FiltroNota, locais: List<String>, user: UserSaci?, sqlLazy: SqlLazy): List<NotaSaida> {
    val sql = "/sqlSaci/findNotaSaida.sql"
    val nfno = filtro.nfno
    val nfse = filtro.nfse
    val listaTipos =
        listOfNotNull(
          if (user?.nfceExpedicao == true) "NFCE" else null,
          if (user?.vendaExpedicao == true) "VENDA" else null,
          if (user?.entRetExpedicao == true) "ENT_RET" else null,
          if (user?.entRetExpedicao == true) "RETIRAF" else null,
          if (user?.transfExpedicao == true) "TRANSFERENCIA" else null,
          if (user?.vendaFExpedicao == true) "VENDAF" else null
        )
    val dataInicial = filtro.dataInicial?.toSaciDate() ?: 0
    val dataFinal = filtro.dataFinal?.toSaciDate() ?: dataInicial
    return if (dataInicial == 0 && dataFinal == 0) emptyList()
    else query(sql, NotaSaida::class, sqlLazy) {
      addOptionalParameter("marca", filtro.marca.num)
      addOptionalParameter("storeno", filtro.storeno)
      addOptionalParameter("nfno", nfno)
      addOptionalParameter("nfse", nfse)
      addOptionalParameter("cliente", filtro.cliente)
      addOptionalParameter("vendedor", filtro.vendedor)
      addOptionalParameter("locais", locais)
      addOptionalParameter("listaTipos", listaTipos)
      addOptionalParameter("dataInicial", dataInicial)
      addOptionalParameter("dataFinal", dataFinal)
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

  fun findPedidoTransf(filtro: FiltroPedidoTransf): List<PedidoTransf> {
    val sql = "/sqlSaci/findPedidoTransf.sql"
    return query(sql, PedidoTransf::class) {
      addOptionalParameter("marca", filtro.marca.num)
      addOptionalParameter("storeno", filtro.storeno)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("autorizado", filtro.autorizado.let { if (it == null) "T" else if (it) "S" else "N" })
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
      addOptionalParameter("impresso", filtro.impresso.let { if (it == null) "T" else if (it) "S" else "N" })
    }
  }

  fun findNota(nfno: Int, nfse: String, date: LocalDate): Nota? {
    val sql = "/sqlSaci/findNota.sql"
    return query(sql, Nota::class) {
      addOptionalParameter("nfno", nfno)
      addOptionalParameter("nfse", nfse)
      addOptionalParameter("data", date.toSaciDate())
    }.firstOrNull()
  }

  fun findPedidoRessu4(filtro: FiltroPedidoRessu4): List<TransfRessu4> {
    val sql = "/sqlSaci/findPedidoRessu4.sql"
    return query(sql, TransfRessu4::class) {
      addOptionalParameter("storeno", filtro.storeno)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
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
    val produtos = query(sql, ProdutoNFS::class) {
      addOptionalParameter("storeno", nfs.loja)
      addOptionalParameter("pdvno", nfs.pdvno)
      addOptionalParameter("xano", nfs.xano)
      addOptionalParameter("marca", marca.num)
      addOptionalParameter("locais", locais)
    }
    produtos.forEach {
      println(it.local)
    }
    return produtos
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

  fun findProdutoPedidoTransf(pedido: PedidoTransf): List<ProdutoPedidoTransf> {
    val sql = "/sqlSaci/findProdutosPedidoVenda.sql"
    return query(sql, ProdutoPedidoTransf::class) {
      addOptionalParameter("storeno", pedido.lojaNoOri)
      addOptionalParameter("ordno", pedido.ordno)
    }
  }

  fun findProdutoRessuprimento(
    pedido: Ressuprimento,
    marca: EMarcaRessuprimento,
    locais: List<String>
  ): List<ProdutoRessuprimento> {
    val sql = "/sqlSaci/findProdutosRessuprimento.sql"
    return query(sql, ProdutoRessuprimento::class) {
      addOptionalParameter("ordno", pedido.numero)
      addOptionalParameter("marca", marca.num)
      addOptionalParameter("locais", locais)
    }
  }

  fun findNotaEntradaRecebido(filtro: FiltroNotaEntrada): List<NotaEntrada> {
    val sql = "/sqlSaci/findNotaEntradaRecebido.sql"
    return query(sql, NotaEntrada::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("ni", filtro.ni)
      addOptionalParameter("nfno", filtro.nfno)
      addOptionalParameter("nfse", filtro.nfse)
      addOptionalParameter("vendno", filtro.vendno)
      addOptionalParameter("chave", filtro.chave)
    }
  }

  fun findNotaEntradaBase(filtro: FiltroNotaEntrada): List<NotaEntrada> {
    val sql = "/sqlSaci/findNotaEntradaBase.sql"
    return query(sql, NotaEntrada::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("ni", filtro.ni)
      addOptionalParameter("nfno", filtro.nfno)
      addOptionalParameter("nfse", filtro.nfse)
      addOptionalParameter("vendno", filtro.vendno)
      addOptionalParameter("chave", filtro.chave)
    }
  }

  fun findNotaEntradaReceber(chave: String): List<NotaEntrada> {
    val sql = "/sqlSaci/findNotaEntradaReceber.sql"
    return query(sql, NotaEntrada::class) {
      addOptionalParameter("chave", chave)
    }
  }

  fun marcaNotaEntradaReceber(chave: String, marca: Int): NotaEntrada? {
    val sql = "/sqlSaci/marcaNotaEntrada.sql"
    script(sql) {
      addOptionalParameter("chave", chave)
      addOptionalParameter("marca", marca)
    }
    return findNotaEntradaReceber(chave).firstOrNull()
  }

  private fun findProdutoNFEPendente(nota: NotaEntrada): List<ProdutoNFE> {
    val sql = "/sqlSaci/findProdutosNFEntradaPendente.sql"
    return query(sql, ProdutoNFE::class) {
      addOptionalParameter("ni", nota.ni)
    }
  }

  fun findProdutoNFERecebido(nota: NotaEntrada): List<ProdutoNFE> {
    val sql = "/sqlSaci/findProdutosNFEntradaRecebido.sql"
    return query(sql, ProdutoNFE::class) {
      addOptionalParameter("nfekey", nota.chave)
    }
  }

  fun findProdutoNFEBase(nota: NotaEntrada): List<ProdutoNFE> {
    val sql = "/sqlSaci/findProdutosNFEntradaBase.sql"
    return query(sql, ProdutoNFE::class) {
      addOptionalParameter("nfekey", nota.chave)
    }
  }

  fun findProdutoNFEReceber(nota: NotaEntrada): List<ProdutoNFE> {
    val sql = "/sqlSaci/findProdutosNFEntradaReceber.sql"
    return query(sql, ProdutoNFE::class) {
      addOptionalParameter("nfekey", nota.chave)
    }
  }

  fun addProdutoReceber(chave: String?, barcode: String, quant: Int) {
    val sql = "/sqlSaci/addProdutoConf.sql"
    return script(sql) {
      addOptionalParameter("nfekey", chave)
      addOptionalParameter("barcode", barcode)
      addOptionalParameter("quant", quant)
    }
  }

  fun updateProdutoReceber(produto: ProdutoNFE) {
    val sql = "/sqlSaci/updateProdutoConf.sql"
    return script(sql) {
      addOptionalParameter("nfekey", produto.chave)
      addOptionalParameter("codigo", produto.codigo)
      addOptionalParameter("grade", produto.grade)
      addOptionalParameter("marca", produto.marca)
      addOptionalParameter("qtty", produto.quantidade)
    }
  }

  fun removerNotaReceber(nota: NotaEntrada) {
    val sql = "/sqlSaci/revoverNotaReceber.sql"
    script(sql) {
      addOptionalParameter("nfekey", nota.chave)
    }
  }

  fun removeProdutoReceber(produto: ProdutoNFE) {
    val sql = "/sqlSaci/revomerProdutoReceber.sql"
    script(sql) {
      addOptionalParameter("nfekey", produto.chave)
      addOptionalParameter("codigo", produto.codigo)
      addOptionalParameter("grade", produto.grade)
    }
  }

  fun autorizaPedidoTransf(pedidoTransf: PedidoTransf, userSing: Int) {
    val sql = "/sqlSaci/autorizaPedidoTransf.sql"
    script(sql) {
      addOptionalParameter("storeno", pedidoTransf.lojaNoOri)
      addOptionalParameter("ordno", pedidoTransf.ordno)
      addOptionalParameter("userSing", userSing)
    }

  }

  fun findProdutoPedidoRessu4(transfRessu4: TransfRessu4): List<ProdutoTransfRessu4> {
    val sql = "/sqlSaci/findProdutosPedidoRessu4.sql"
    return query(sql, ProdutoTransfRessu4::class) {
      addOptionalParameter("loja", transfRessu4.loja)
      addOptionalParameter("pdvno", transfRessu4.pdvno)
      addOptionalParameter("transacao", transfRessu4.transacao)
    }
  }

  fun entradaDevCli(filtro: FiltroEntradaDevCli): List<EntradaDevCli> {
    val sql = "/sqlSaci/entradaDevCli.sql"
    return query(sql, EntradaDevCli::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("dataI", filtro.dataI.toSaciDate())
      addOptionalParameter("dataF", filtro.dataF.toSaciDate())
      addOptionalParameter("query", filtro.query)
      addOptionalParameter("impresso", filtro.impresso.let { if (it) "S" else "N" })
    }
  }

  fun entradaDevCliPro(invno: Int): List<EntradaDevCliPro> {
    val sql = "/sqlSaci/entradaDevCliPro.sql"
    return query(sql, EntradaDevCliPro::class) {
      addOptionalParameter("invno", invno)
    }
  }

  fun marcaImpresso(invno: Int, impressora: Impressora) {
    val sql = "/sqlSaci/marcaImpresso.sql"
    script(sql) {
      addOptionalParameter("invno", invno)
      addOptionalParameter("marca", impressora.name)
    }
  }

  fun marcaPedidoImpresso(storeno: Int, ordno: Int, impressora: Impressora) {
    val sql = "/sqlSaci/marcaPedidoImpresso.sql"
    script(sql) {
      addOptionalParameter("storeno", storeno)
      addOptionalParameter("ordno", ordno)
      addOptionalParameter("numPrinter", impressora.no)
    }
  }

  fun entradaDevCliProList(filtro: FiltroEntradaDevCliProList): List<EntradaDevCliProList> {
    val sql = "/sqlSaci/entradaDevCliProList.sql"
    return query(sql, EntradaDevCliProList::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("data", filtro.data.toSaciDate())
    }
  }

  fun mudaParaReservado(storeno: Int, ordno: Int, user: Int) {
    val sql = "/sqlSaci/mudaParaReservado.sql"
    script(sql) {
      addOptionalParameter("storeno", storeno)
      addOptionalParameter("ordno", ordno)
      addOptionalParameter("user", user)
    }
  }

  companion object {
    private val db = DB("saci")
    val ipServer: String? = db.url.split("/").getOrNull(2)

    internal val database = DatabaseConfig(
      driver = db.driver,
      url = db.url,
      user = db.username,
      password = db.password
    )
  }
}

val saci = QuerySaci()
