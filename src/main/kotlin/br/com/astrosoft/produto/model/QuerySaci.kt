package br.com.astrosoft.produto.model

import br.com.astrosoft.framework.model.DB
import br.com.astrosoft.framework.model.DatabaseConfig
import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.config.AppConfig.appName
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.beans.*
import org.sql2o.Query
import java.time.LocalDate
import java.time.LocalTime

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

  fun statusPedido(pedido: DadosPedido, status: EStatusPedido) {
    val sql = "/sqlSaci/expiraPedido.sql"
    script(sql) {
      addOptionalParameter("loja", pedido.loja)
      addOptionalParameter("pedido", pedido.pedido)
      addOptionalParameter("status", status.codigo)
    }
  }

  fun statusPedido(pedido: Reposicao, status: EStatusPedido) {
    val sql = "/sqlSaci/expiraPedido.sql"
    script(sql) {
      addOptionalParameter("loja", pedido.loja)
      addOptionalParameter("pedido", pedido.numero)
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

  fun salvaProdutosNFS(produtoNF: ProdutoNFS) {
    val sql = "/sqlSaci/salvaProdutosNFS.sql"
    script(sql) {
      addOptionalParameter("storeno", produtoNF.loja)
      addOptionalParameter("pdvno", produtoNF.pdvno)
      addOptionalParameter("xano", produtoNF.xano)
      addOptionalParameter("codigo", produtoNF.codigo)
      addOptionalParameter("grade", produtoNF.grade)
      addOptionalParameter("gradeAlternativa", produtoNF.gradeAlternativa)
      addOptionalParameter("marca", produtoNF.marca ?: 0)
      addOptionalParameter("marcaImpressao", produtoNF.marcaImpressao ?: 0)
      addOptionalParameter("usuarioCD", produtoNF.usuarioCD)
      addOptionalParameter("usuarioExp", produtoNF.usuarioExp)
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

  fun salvaProdutosRessuprimento(produtoPedidoVenda: ProdutoRessuprimento) {
    val sql = "/sqlSaci/salvaProdutosRessuprimento.sql"
    script(sql) {
      addOptionalParameter("ordno", produtoPedidoVenda.ordno ?: 0)
      addOptionalParameter("codigo", produtoPedidoVenda.codigo ?: "")
      addOptionalParameter("grade", produtoPedidoVenda.grade ?: "")
      addOptionalParameter("marca", produtoPedidoVenda.marca ?: 0)
      addOptionalParameter("selecionado", produtoPedidoVenda.selecionado ?: 0)
      addOptionalParameter("qtEntregue", produtoPedidoVenda.qtEntregue ?: 0)
      addOptionalParameter("qtRecebido", produtoPedidoVenda.qtRecebido ?: 0)
      addOptionalParameter("qtAvaria", produtoPedidoVenda.qtAvaria ?: 0)
      addOptionalParameter("qtVencido", produtoPedidoVenda.qtVencido ?: 0)
      addOptionalParameter("codigoCorrecao", produtoPedidoVenda.codigoCorrecao)
      addOptionalParameter("gradeCorrecao", produtoPedidoVenda.gradeCorrecao)
    }
  }

  fun findNotaSaida(filtro: FiltroNota): List<NotaSaida> {
    val sql = "/sqlSaci/findNotaSaida.sql"
    val user = AppConfig.userLogin() as? UserSaci

    val dataInicial = filtro.dataInicial?.toSaciDate() ?: 0
    val dataFinal = filtro.dataFinal?.toSaciDate() ?: 0
    return query(sql, NotaSaida::class) {
      addOptionalParameter("marca", filtro.marca.num)
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("lojaLocal", user?.lojaLocExpedicao ?: 0)
      addOptionalParameter("locais", user?.localizacaoNota?.toList() ?: listOf("TODOS"))
      addOptionalParameter("dataInicial", dataInicial)
      addOptionalParameter("dataFinal", dataFinal)
      addOptionalParameter("notaEntrega", filtro.notaEntrega)
    }.filter {
      when (filtro.tipoNota) {
        ETipoNotaFiscal.SIMP_REME_L -> it.retiraFutura == true &&
                                       it.tipoNotaSaida == ETipoNotaFiscal.SIMP_REME.name &&
                                       it.loja != filtro.loja &&
                                       filtro.loja != 0

        ETipoNotaFiscal.SIMP_REME   -> it.retiraFutura == true &&
                                       it.tipoNotaSaida == ETipoNotaFiscal.SIMP_REME.name &&
                                       it.loja == filtro.loja &&
                                       filtro.loja != 0 &&
                                       it.serie == "3"

        else                        -> it.tipoNotaSaida == filtro.tipoNota.name || filtro.tipoNota == ETipoNotaFiscal.TODOS
      }
    }
  }

  fun findPedidoVenda(filtro: FiltroPedidoVenda, locais: List<String>): List<PedidoVenda> {
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
      addOptionalParameter("temNota", filtro.temNota.codigo)
      addOptionalParameter("ordno", filtro.numero)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("locais", locais)
      addOptionalParameter("lojaRessu", filtro.lojaRessu)
      addOptionalParameter("dataPedidoInicial", filtro.dataPedidoInicial.toSaciDate())
      addOptionalParameter("dataPedidoFinal", filtro.dataPedidoFinal.toSaciDate())
      addOptionalParameter("dataNotaInicial", filtro.dataNotaInicial.toSaciDate())
      addOptionalParameter("dataNotaFinal", filtro.dataNotaFinal.toSaciDate())
      addOptionalParameter("codigo", filtro.codigo)
      addOptionalParameter("grade", filtro.grade)
    }
  }

  fun findProdutoNF(nfs: NotaSaida, marca: EMarcaNota): List<ProdutoNFS> {
    val sql = "/sqlSaci/findProdutosNFSaida.sql"
    val user = AppConfig.userLogin() as? UserSaci
    val produtos = query(sql, ProdutoNFS::class) {
      addOptionalParameter("storeno", nfs.loja)
      addOptionalParameter("pdvno", nfs.pdvno)
      addOptionalParameter("xano", nfs.xano)
      addOptionalParameter("marca", marca.num)
      addOptionalParameter("lojaLocal", user?.lojaLocExpedicao ?: 0)
      addOptionalParameter("locais", user?.localizacaoNota?.toList() ?: listOf("TODOS"))
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
    val localList = pedido.localList()
    return query(sql, ProdutoRessuprimento::class) {
      addOptionalParameter("ordno", pedido.numero)
      addOptionalParameter("marca", marca.num)
      addOptionalParameter("locApp", localList)
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
      addOptionalParameter("tipo", filtro.tipo.codigo)
      addOptionalParameter("dataLimiteInicial", filtro.dataLimiteInicial.toSaciDate())
      addOptionalParameter("impresso", filtro.impresso.let {
        when {
          it == null -> "T"
          it         -> "S"
          else       -> "N"
        }
      })
    }
  }

  fun entradaDevCliPro(invno: Int): List<EntradaDevCliPro> {
    val sql = "/sqlSaci/entradaDevCliPro.sql"
    return query(sql, EntradaDevCliPro::class) {
      addOptionalParameter("invno", invno)
    }
  }

  fun marcaImpresso(invno: Int, storeno: Int, pdvno: Int, xano: Int, impressora: Impressora) {
    val sql = "/sqlSaci/marcaImpresso.sql"
    script(sql) {
      addOptionalParameter("invno", invno)
      addOptionalParameter("marca", impressora.name)
      addOptionalParameter("storeno", storeno)
      addOptionalParameter("pdvno", pdvno)
      addOptionalParameter("xano", xano)
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
      addOptionalParameter("pesquisa", filtro.pesquisa)
    }
  }

  fun entradaDevCliProList(listNi: List<Int>): List<EntradaDevCliProList> {
    val sql = "/sqlSaci/entradaDevCliProListNi.sql"
    return query(sql, EntradaDevCliProList::class) {
      addOptionalParameter("listNi", listNi)
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

  fun ativaMarca(storeno: Int, ordno: Int, marca: String) {
    val sql = "/sqlSaci/ativaMarca.sql"
    script(sql) {
      addOptionalParameter("storeno", storeno)
      addOptionalParameter("ordno", ordno)
      addOptionalParameter("marca", marca)
    }
  }

  /*
    fun marcaSeparado(storeno: Int, ordno: Int, marca: String) {
      val sql = "/sqlSaci/marcaSeparado.sql"
      script(sql) {
        addOptionalParameter("storeno", storeno)
        addOptionalParameter("ordno", ordno)
        addOptionalParameter("marca", marca)
      }
    }
  */
  fun ativaDataHoraImpressao(storeno: Int, ordno: Int, data: LocalDate?, hora: LocalTime?, userno: Int) {
    val sql = "/sqlSaci/ativaDataHoraImpressao.sql"
    script(sql) {
      addParameter("storeno", storeno)
      addParameter("ordno", ordno)
      addParameter("data", data?.toSaciDate() ?: 0)
      addParameter("hora", hora ?: LocalTime.MIN)
      addParameter("userno", userno)
    }
  }

  fun produtoPedido(storeno: Int, ordno: Int, tipo: String): List<ProdutoPedido> {
    val sql = "/sqlSaci/produtoPedido.sql"
    return query(sql, ProdutoPedido::class) {
      addOptionalParameter("storeno", storeno)
      addOptionalParameter("ordno", ordno)
      addOptionalParameter("tipo", tipo)
    }
  }

  /*
    fun marcaCarga(storeno: Int, ordno: Int, carga: EZonaCarga, entrega: LocalDate?) {
      val sql = "/sqlSaci/marcaCarga.sql"
      val dataEntrega = entrega.toSaciDate().toString()
      script(sql) {
        addOptionalParameter("storeno", storeno)
        addOptionalParameter("ordno", ordno)
        addOptionalParameter("marca", carga.codigo.toString())
        addOptionalParameter("entrega", dataEntrega)
      }
    }
  */
  fun listaPedido(filtro: FiltroPedido): List<Pedido> {
    val sql = "/sqlSaci/listaPedido.sql"

    return query(sql, Pedido::class) {
      addOptionalParameter("tipo", filtro.tipo.sigla)
      addOptionalParameter("storeno", filtro.loja)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
      addOptionalParameter("pesquisa", filtro.pesquisa.trim())
      addOptionalParameter("tipoRetira", filtro.tipoRetira.name)
    }
  }

  fun marcaMudaCliente(saldoDevolucao: SaldoDevolucao) {
    val sql = "/sqlSaci/updateSaldoMuda.sql"
    script(sql) {
      addOptionalParameter("invno", saldoDevolucao.invno)
      addOptionalParameter("custnoDev", saldoDevolucao.custnoDev)
      addOptionalParameter("custnoMuda", saldoDevolucao.custnoMuda)
      addOptionalParameter("saldo", (saldoDevolucao.saldo * 100.00).toInt())
    }
  }

  fun marcaReembolso(saldoDevolucao: SaldoDevolucao) {
    val sql = "/sqlSaci/updateSaldoReembolso.sql"
    script(sql) {
      addOptionalParameter("invno", saldoDevolucao.invno)
      addOptionalParameter("custnoDev", saldoDevolucao.custnoDev)
      addOptionalParameter("custnoMuda", saldoDevolucao.custnoMuda)
      addOptionalParameter("tipo", saldoDevolucao.tipo)
      addOptionalParameter("nfdev", saldoDevolucao.notaDev?.notaFiscal ?: "")
      addOptionalParameter("loja", saldoDevolucao.notaDev?.loja ?: 0)
      addOptionalParameter("nfno", saldoDevolucao.notaDev?.nfVenda?.split("/")?.getOrNull(0)?.toInt() ?: 0)
      addOptionalParameter("nfse", saldoDevolucao.notaDev?.nfVenda?.split("/")?.getOrNull(1) ?: "")
      addOptionalParameter("saldo", (saldoDevolucao.saldo * 100.00).toInt())
    }
  }

  fun findCreditoCliente(filtro: FiltroCreditoCliente): List<CreditoCliente> {
    val sql = "/sqlSaci/findCredito.sql"
    return query(sql, CreditoCliente::class) {
      addOptionalParameter("pesquisa", filtro.pesquisa)
    }
  }

  fun findDevTroca(filtro: FiltroDevTroca): List<DevTroca> {
    val sql = "/sqlSaci/findDevTroca.sql"
    return query(sql, DevTroca::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
    }
  }

  fun mudaCliente(codigo: Int): Cliente? {
    val sql = "/sqlSaci/findCliente.sql"
    return query(sql, Cliente::class) {
      addOptionalParameter("codigo", codigo)
    }.firstOrNull()
  }

  fun findLojaNaoInformada(custno: Int): Cliente? {
    val sql = "/sqlSaci/findLojaNaoInformada.sql"
    return query(sql, Cliente::class) {
      addOptionalParameter("custno", custno)
    }.firstOrNull()
  }

  fun findAcertoEstoqueEntrada(filtro: FiltroAcertoEntrada): List<AcertoEntrada> {
    val sql = "/sqlSaci/acertoEstoqueEntrada.sql"
    return query(sql, AcertoEntrada::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.query)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
    }
  }

  fun findAcertoEstoqueSaida(filtro: FiltroAcertoSaida): List<AcertoSaida> {
    val sql = "/sqlSaci/acertoEstoqueSaida.sql"
    return query(sql, AcertoSaida::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.query)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
    }
  }

  fun findNotaAutorizacao(loja: Int, nfno: Int, nfse: String): List<NotaAutorizacao> {
    val sql = "/sqlSaci/notaAutorizacaoSimples.sql"
    return query(sql, NotaAutorizacao::class) {
      addOptionalParameter("loja", loja)
      addOptionalParameter("nfno", nfno)
      addOptionalParameter("nfse", nfse)
    }
  }

  fun findNotaAutorizacao(filtro: FiltroNotaAutorizacao): List<NotaAutorizacao> {
    val sql = "/sqlSaci/notaAutorizacao.sql"
    return query(sql, NotaAutorizacao::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
    }
  }

  fun deleteNotaAutorizacao(nota: NotaAutorizacao) {
    val sql = "/sqlSaci/notaAutorizacaoDelete.sql"
    script(sql) {
      addOptionalParameter("loja", nota.loja ?: 0)
      addOptionalParameter("pdv", nota.pdv ?: 0)
      addOptionalParameter("transacao", nota.transacao ?: 0)
    }
  }

  fun insertNotaAutorizacao(nota: NotaAutorizacaoChave) {
    val sql = "/sqlSaci/notaAutorizacaoInsert.sql"
    script(sql) {
      addOptionalParameter("loja", nota.loja)
      addOptionalParameter("nfno", nota.nfno)
      addOptionalParameter("nfse", nota.nfse)
    }
  }

  fun updateNotaAutorizacao(nota: NotaAutorizacao) {
    val sql = "/sqlSaci/notaAutorizacaoUpdate.sql"
    script(sql) {
      addOptionalParameter("loja", nota.loja ?: 0)
      addOptionalParameter("pdv", nota.pdv ?: 0)
      addOptionalParameter("transacao", nota.transacao ?: 0)
      addOptionalParameter("usernoSing", nota.usernoSing ?: 0)
      addOptionalParameter("tipoDev", nota.tipoDev ?: "")
      addOptionalParameter("observacao", nota.observacao ?: "")
      addOptionalParameter("impresso", nota.impresso ?: "N")
    }
  }

  fun findNotaVenda(filtro: FiltroNotaVenda): List<NotaVenda> {
    val sql = "/sqlSaci/vendas.sql"
    return query(sql, NotaVenda::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
    }
  }

  fun findNotaVendaRef(filtro: FiltroNotaVendaRef): List<NotaVendaRef> {
    val sql = "/sqlSaci/vendasRef.sql"
    return query(sql, NotaVendaRef::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
    }
  }

  fun autorizaNota(invno: Int, storeno: Int, pdvno: Int, xano: Int, user: UserSaci) {
    val sql = "/sqlSaci/autorizaNota.sql"
    script(sql) {
      addOptionalParameter("invno", invno)
      addOptionalParameter("storeno", storeno)
      addOptionalParameter("pdvno", pdvno)
      addOptionalParameter("xano", xano)
      addOptionalParameter("userno", user.no)
    }
  }

  fun findMovManual(filter: MovManualFilter): List<MovManual> {
    val sql = "/sqlSaci/acertoEstoqueMovManual.sql"
    return query(sql, MovManual::class) {
      addOptionalParameter("loja", filter.loja)
      addOptionalParameter("pesquisa", filter.query)
      addOptionalParameter("dataInicial", filter.dataI.toSaciDate())
      addOptionalParameter("dataFinal", filter.dataF.toSaciDate())
      addOptionalParameter("tipo", filter.tipo.codigo)
    }
  }

  fun estornoMovManual(movManual: MovManual) {
    val sql = "/sqlSaci/acertoEstoqueEstornoMovManual.sql"
    val loja = movManual.loja ?: return
    val prdno = movManual.codigoProduto?.toString()?.lpad(16, " ") ?: return
    val grade = movManual.grade ?: return
    val xano = movManual.transacao ?: return
    script(sql) {
      addOptionalParameter("storeno", loja)
      addOptionalParameter("prdno", prdno)
      addOptionalParameter("grade", grade)
      addOptionalParameter("xano", xano)
    }
  }

  fun findProdutoSaldo(filtro: FiltroProdutoSaldo): List<ProdutoSaldo> {
    val sql = "/sqlSaci/findProdutosSaldo.sql"
    return query(sql, ProdutoSaldo::class) {
      this.addOptionalParameter("loja", filtro.loja)
      this.addOptionalParameter("pesquisa", filtro.pesquisa)
      this.addOptionalParameter("fornecedor", filtro.fornecedor)
      this.addOptionalParameter("tributacao", filtro.tributacao)
      this.addOptionalParameter("rotulo", filtro.rotulo)
      this.addOptionalParameter("tipo", filtro.tipo)
      this.addOptionalParameter("cl", filtro.cl)
      this.addOptionalParameter("caracter", filtro.caracter.value)
      this.addOptionalParameter("letraDup", filtro.letraDup.value)
      this.addOptionalParameter("grade", filtro.grade.let { if (it) "S" else "N" })
      this.addOptionalParameter("estoque", filtro.estoque.value)
      this.addOptionalParameter("saldo", filtro.saldo)
    }
  }

  fun findProdutoEstoque(filter: FiltroProdutoEstoque): List<ProdutoEstoque> {
    val sql = "/sqlSaci/findProdutoEstoque.sql"
    val user = AppConfig.userLogin() as? UserSaci
    val listaUser = user?.listaEstoque.orEmpty().toList().ifEmpty {
      listOf("TODOS")
    }
    return query(sql, ProdutoEstoque::class) {
      addOptionalParameter("loja", filter.loja)
      addOptionalParameter("pesquisa", filter.pesquisa)
      addOptionalParameter("grade", filter.grade)
      addOptionalParameter("codigo", filter.codigo)
      addOptionalParameter("caracter", filter.caracter.value)
      addOptionalParameter("fornecedor", filter.fornecedor)
      addOptionalParameter("localizacaoUser", listaUser)
      addOptionalParameter("localizacao", filter.localizacao)
    }
  }

  fun autorizaRessuprimento(ressuprimento: Ressuprimento) {
    val sql = "/sqlSaci/autorizaRessuprimento.sql"
    script(sql) {
      addOptionalParameter("ordno", ressuprimento.numero)
      addOptionalParameter("storeno", 1)
      addOptionalParameter("userno", ressuprimento.entregueNo ?: 0)
    }
  }

  fun entregueRessuprimento(ressuprimento: Ressuprimento) {
    val sql = "/sqlSaci/entregueRessuprimento.sql"
    script(sql) {
      addOptionalParameter("ordno", ressuprimento.numero)
      addOptionalParameter("storeno", 1)
      addOptionalParameter("singno", ressuprimento.entregueNo ?: 0)
    }
  }

  fun recebeRessuprimento(ressuprimento: Ressuprimento) {
    val sql = "/sqlSaci/recebeRessuprimento.sql"
    script(sql) {
      addOptionalParameter("ordno", ressuprimento.numero)
      addOptionalParameter("storeno", 1)
      addOptionalParameter("recebidoNo", ressuprimento.recebidoNo ?: 0)
    }
  }

  fun transportadoRessuprimento(ressuprimento: Ressuprimento) {
    val sql = "/sqlSaci/transportadoRessuprimento.sql"
    script(sql) {
      addOptionalParameter("ordno", ressuprimento.numero)
      addOptionalParameter("storeno", 1)
      addOptionalParameter("transportadoNo", ressuprimento.transportadoNo ?: 0)
    }
  }

  fun devolvidoRessuprimento(ressuprimento: Ressuprimento) {
    val sql = "/sqlSaci/devolvidoRessuprimento.sql"
    script(sql) {
      addOptionalParameter("ordno", ressuprimento.numero)
      addOptionalParameter("storeno", 1)
      addOptionalParameter("devolvidoNo", ressuprimento.devolvidoNo ?: 0)
    }
  }

  fun updateProdutoEstoque(produtoEstoque: ProdutoEstoque) {
    val sql = "/sqlSaci/updateProdutoEstoque.sql"

    script(sql) {
      addOptionalParameter("loja", produtoEstoque.loja ?: 0)
      addOptionalParameter("prdno", produtoEstoque.prdno ?: "")
      addOptionalParameter("grade", produtoEstoque.grade ?: "")
      addOptionalParameter("estoque", produtoEstoque.estoque ?: 0)
      addOptionalParameter("locApp", produtoEstoque.locApp)
    }
  }

  fun excluiRessuprimento(ressuprimento: Ressuprimento) {
    val sql = "/sqlSaci/excluiRessuprimento.sql"
    script(sql) {
      addOptionalParameter("ordno", ressuprimento.numero)
      addOptionalParameter("storeno", 1)
      addOptionalParameter("marca", ressuprimento.marca ?: 0)
    }
  }

  fun salvaRessuprimento(ressuprimento: Ressuprimento) {
    val sql = "/sqlSaci/salvaRessuprimento.sql"
    script(sql) {
      addOptionalParameter("ordno", ressuprimento.numero)
      addOptionalParameter("storeno", 1)
      addOptionalParameter("localizacoes", ressuprimento.localizacoes)
      addOptionalParameter("observacao", ressuprimento.observacao)
    }
  }

  fun excluiProdutosRessuprimento(ressuprimento: Ressuprimento) {
    val produtos = ressuprimento.produtos()
    val sql = "/sqlSaci/excluiProdutosRessuprimento.sql"
    produtos.forEach { produto ->
      script(sql) {
        addOptionalParameter("ordno", ressuprimento.numero)
        addOptionalParameter("storeno", 1)
        addOptionalParameter("prdno", produto.codigo.lpad(16, " "))
        addOptionalParameter("grade", produto.grade)
      }
    }
  }

  fun findResposicaoProduto(filtro: FiltroReposicao): List<ReposicaoProduto> {
    val sql = "/sqlSaci/reposicaoProdutos.sql"
    val datacorte = DB.test.let { if (it) 20240101 else 20240318 }

    return query(sql, ReposicaoProduto::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("localizacao", filtro.localizacao)
      addOptionalParameter("datacorte", datacorte)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
      addOptionalParameter("codigo", filtro.codigo)
      addOptionalParameter("grade", filtro.grade)
    }
  }

  fun updateReposicao(reposicao: Reposicao) {
    val sql = "/sqlSaci/updateReposicao.sql"
    script(sql) {
      addOptionalParameter("loja", reposicao.loja)
      addOptionalParameter("numero", reposicao.numero)
      addOptionalParameter("localizacao", reposicao.localizacao)
      addOptionalParameter("recebidoNo", reposicao.recebidoNo)
      addOptionalParameter("entregueNo", reposicao.entregueNo)
      addOptionalParameter("observacao", reposicao.observacao)
    }
  }

  fun updateReposicaoProduto(reposicaoProduto: ReposicaoProduto) {
    val sql = "/sqlSaci/updateReposicaoProduto.sql"
    script(sql) {
      addOptionalParameter("loja", reposicaoProduto.loja ?: 0)
      addOptionalParameter("numero", reposicaoProduto.numero ?: 0)
      addOptionalParameter("prdno", reposicaoProduto.prdno)
      addOptionalParameter("grade", reposicaoProduto.grade)
      addOptionalParameter("marca", reposicaoProduto.marca ?: 0)
      addOptionalParameter("qtRecebido", reposicaoProduto.qtRecebido ?: 0)
      addOptionalParameter("selecionado", reposicaoProduto.selecionado ?: 0)
      addOptionalParameter("posicao", reposicaoProduto.posicao ?: 0)
    }
  }

  fun findNotaRecebimentoProduto(filtro: FiltroNotaRecebimentoProduto): List<NotaRecebimentoProduto> {
    val sql = "/sqlSaci/findNotaRecebimentoProduto.sql"
    return query(sql, NotaRecebimentoProduto::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("marca", filtro.marca.codigo)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
      addOptionalParameter("invno", filtro.invno)
      addOptionalParameter("localizacao", filtro.localizacao)
    }
  }

  fun updateNotaRecebimentoProduto(notaRecebimentoProduto: NotaRecebimentoProduto) {
    val sql = "/sqlSaci/findNotaRecebimentoProdutoUpdate.sql"
    script(sql) {
      addOptionalParameter("ni", notaRecebimentoProduto.ni ?: 0)
      addOptionalParameter("prdno", notaRecebimentoProduto.prdno ?: "")
      addOptionalParameter("grade", notaRecebimentoProduto.grade ?: "")
      addOptionalParameter("marca", notaRecebimentoProduto.marca ?: 0)
      addOptionalParameter("login", notaRecebimentoProduto.login ?: "")
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

class Count {
  var value: Int = 0
}