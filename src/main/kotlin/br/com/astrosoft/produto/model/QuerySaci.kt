package br.com.astrosoft.produto.model

import br.com.astrosoft.devolucao.model.beans.Agenda
import br.com.astrosoft.devolucao.model.beans.FiltroAgenda
import br.com.astrosoft.framework.model.DB
import br.com.astrosoft.framework.model.DatabaseConfig
import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.config.AppConfig.appName
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.beans.*
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

  fun findGrades(codigo: String, loja: Int = 4): List<PrdGrade> {
    val sql = "/sqlSaci/findGrades.sql"
    return query(sql, PrdGrade::class) {
      addOptionalParameter("codigo", codigo)
      addOptionalParameter("loja", loja)
    }
  }

  fun findProdutoGrades(codigo: String): List<PrdGrade> {
    val sql = "/sqlSaci/findProdutoGrades.sql"
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
      addOptionalParameter("bitAcesso2", user.bitAcesso2)
      addOptionalParameter("loja", user.storeno)
      addOptionalParameter("appName", appName)
      addOptionalParameter("locais", user.locais)
      addOptionalParameter("listaImpressora", user.listaImpressora)
      addOptionalParameter("listaLoja", user.listaLoja)
    }
  }

  fun listFuncionario(codigo: Int): Funcionario? {
    val sql = "/sqlSaci/listFuncionario.sql"
    return query(sql, Funcionario::class) {
      addOptionalParameter("codigo", codigo)
    }.firstOrNull()
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
      addOptionalParameter("usuarioSep", produtoNF.usuarioSep)
      addOptionalParameter("usuarioCD", produtoNF.usernoCD ?: 0)
      addOptionalParameter("usuarioExp", produtoNF.usernoExp ?: 0)
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

  fun saveNotaSaida(nota: NotaSaida) {
    val sql = "/sqlSaci/saveNotaSaida.sql"
    script(sql) {
      this.addOptionalParameter("storeno", nota.loja)
      this.addOptionalParameter("pdvno", nota.pdvno)
      this.addOptionalParameter("xano", nota.xano)
      this.addOptionalParameter("entrega", nota.entrega.toSaciDate())
      this.addOptionalParameter("empnoM", nota.empnoMotorista ?: 0)
    }
  }

  fun saveNotaSaida(nota: NotaSaidaDev) {
    val sql = "/sqlSaci/saveNotaSaida.sql"
    script(sql) {
      this.addOptionalParameter("storeno", nota.loja)
      this.addOptionalParameter("pdvno", nota.pdvno)
      this.addOptionalParameter("xano", nota.xano)
      this.addOptionalParameter("entrega", nota.entrega.toSaciDate())
      this.addOptionalParameter("empnoM", 0)
    }
  }

  fun saveNotaSaidaPrint(nota: NotaSaida) {
    val sql = "/sqlSaci/saveNotaSaidaPrint.sql"
    script(sql) {
      this.addOptionalParameter("storeno", nota.loja)
      this.addOptionalParameter("pdvno", nota.pdvno)
      this.addOptionalParameter("xano", nota.xano)
      this.addOptionalParameter("userPrint", nota.usernoPrint ?: 0)
      this.addOptionalParameter("usernoSing", nota.usernoSingCD ?: 0)
      this.addOptionalParameter("usernoSingExp", nota.usernoSingExp ?: 0)
    }
  }

  fun findNotaSaidaDevolucao(filtro: FiltroNotaDev): List<NotaSaidaDev> {
    val sql = "/sqlSaci/findNotaSaidaDevolucao.sql"

    val list = query(sql, NotaSaidaDev::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("local", filtro.localizacaoNota)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
      addOptionalParameter("prdno", filtro.prdno)
      addOptionalParameter("grade", filtro.grade)
    }
    return list
  }

  fun findNotaSaida(filtro: FiltroNota): List<NotaSaida> {
    val sql = "/sqlSaci/findNotaSaida.sql"

    val list = query(sql, NotaSaida::class) {
      addOptionalParameter("marca", filtro.marca.num)
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("local", filtro.localizacaoNota)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
      addOptionalParameter("dataEntregaInicial", filtro.dataEntregaInicial.toSaciDate())
      addOptionalParameter("dataEntregaFinal", filtro.dataEntregaFinal.toSaciDate())
      addOptionalParameter("prdno", filtro.prdno)
      addOptionalParameter("grade", filtro.grade)
    }

    println("list.size: ${list.size}")
    println(filtro)

    val listFilter = list.filter {
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

    println("listFilter.size: ${listFilter.size}")

    return listFilter
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

  fun findPedidoTransf(filtro: FiltroPedidoTransf, filtraCD5A: Boolean): List<PedidoTransf> {
    val sql = "/sqlSaci/findPedidoTransf.sql"
    return query(sql, PedidoTransf::class) {
      addOptionalParameter("marca", filtro.marca.num)
      addOptionalParameter("storeno", filtro.storeno)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("autorizado", filtro.autorizado.let { if (it == null) "T" else if (it) "S" else "N" })
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
      addOptionalParameter("impresso", filtro.impresso.let { if (it == null) "T" else if (it) "S" else "N" })
      addOptionalParameter("filtraCD5A", filtraCD5A.let { if (it) "S" else "N" })
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
      addOptionalParameter("local", locais)
      addOptionalParameter("lojaRessu", filtro.lojaRessu)
      addOptionalParameter("dataPedidoInicial", filtro.dataPedidoInicial.toSaciDate())
      addOptionalParameter("dataPedidoFinal", filtro.dataPedidoFinal.toSaciDate())
      addOptionalParameter("dataNotaInicial", filtro.dataNotaInicial.toSaciDate())
      addOptionalParameter("dataNotaFinal", filtro.dataNotaFinal.toSaciDate())
      addOptionalParameter("prdno", filtro.prdno)
      addOptionalParameter("grade", filtro.grade)
    }
  }

  fun findProdutoNF(
    nfs: NotaSaida,
    marca: EMarcaNota,
    prdno: String,
    grade: String,
    todosLocais: Boolean
  ): List<ProdutoNFS> {
    val sql = "/sqlSaci/findProdutosNFSaida.sql"
    val user = if (prdno == "") AppConfig.userLogin() as? UserSaci else null
    val produtos = query(sql, ProdutoNFS::class) {
      addOptionalParameter("storeno", nfs.loja)
      addOptionalParameter("pdvno", nfs.pdvno)
      addOptionalParameter("xano", nfs.xano)
      addOptionalParameter("marca", marca.num)
      addOptionalParameter("prdno", prdno)
      addOptionalParameter("grade", grade)
      addOptionalParameter("lojaLocal", 4)
      addOptionalParameter("todosLocais", todosLocais.let { if (it) "S" else "N" })
      addOptionalParameter("local", user?.localizacaoNota?.toList() ?: listOf("TODOS"))
    }
    produtos.forEach {
      println(it.local)
    }
    return produtos
  }

  fun findProdutoNF(
    nfs: NotaSaidaDev,
    prdno: String,
    grade: String,
    todosLocais: Boolean
  ): List<ProdutoNFS> {
    val sql = "/sqlSaci/findProdutosNFSaida.sql"
    val user = if (prdno == "") AppConfig.userLogin() as? UserSaci else null
    val produtos = query(sql, ProdutoNFS::class) {
      addOptionalParameter("storeno", nfs.loja)
      addOptionalParameter("pdvno", nfs.pdvno)
      addOptionalParameter("xano", nfs.xano)
      addOptionalParameter("marca", 999)
      addOptionalParameter("prdno", prdno)
      addOptionalParameter("grade", grade)
      addOptionalParameter("lojaLocal", 4)
      addOptionalParameter("todosLocais", todosLocais.let { if (it) "S" else "N" })
      addOptionalParameter("local", user?.localizacaoNota?.toList() ?: listOf("TODOS"))
    }
    produtos.forEach {
      println(it.local)
    }
    return produtos
  }

  fun findProdutoNF(prd: ProdutoNFS): List<ProdutoNFS> {
    val sql = "/sqlSaci/findProdutosNFSaida.sql"
    val produtos = query(sql, ProdutoNFS::class) {
      addOptionalParameter("storeno", prd.loja)
      addOptionalParameter("pdvno", prd.pdvno)
      addOptionalParameter("xano", prd.xano)
      addOptionalParameter("marca", EMarcaNota.TODOS.num)
      addOptionalParameter("prdno", prd.codigo.lpad(16, " "))
      addOptionalParameter("grade", prd.grade)
      addOptionalParameter("lojaLocal", 0)
      addOptionalParameter("todosLocais", "S")
      addOptionalParameter("local", listOf("TODOS"))
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
    prdno: String,
    grade: String,
    marca: EMarcaRessuprimento,
    locais: List<String>
  ): List<ProdutoRessuprimento> {
    val sql = "/sqlSaci/findProdutosRessuprimento.sql"
    //val localList = pedido.localList()
    return query(sql, ProdutoRessuprimento::class) {
      addOptionalParameter("ordno", pedido.numero)
      addOptionalParameter("marca", marca.num)
      //addOptionalParameter("locApp", localList)
      addOptionalParameter("local", locais)
      addOptionalParameter("prdno", prdno)
      addOptionalParameter("grade", grade)
      addOptionalParameter("ressu", "S")
    }
  }

  fun findProdutoRessuprimento(pedido: PedidoRessuprimento, ressu: Boolean = true): List<ProdutoRessuprimento> {
    val sql = "/sqlSaci/findProdutosRessuprimento.sql"
    return query(sql, ProdutoRessuprimento::class) {
      addOptionalParameter("ordno", pedido.pedido ?: 0)
      addOptionalParameter("marca", 999)
      addOptionalParameter("local", listOf("TODOS"))
      addOptionalParameter("prdno", "")
      addOptionalParameter("grade", "")
      addOptionalParameter("ressu", ressu.let { if (it) "S" else "N" })
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
      this.addOptionalParameter("tipoSaldo", filtro.tipoSaldo.name)
      this.addOptionalParameter("estoque", filtro.estoque.value)
      this.addOptionalParameter("saldo", filtro.saldo)
      this.addOptionalParameter("update", filtro.update)
    }
  }

  fun findProdutoEstoque(filter: FiltroProdutoEstoque): List<ProdutoEstoque> {
    val sql = "/sqlSaci/findProdutoEstoque.sql"

    return query(sql, ProdutoEstoque::class) {
      addOptionalParameter("loja", filter.loja)
      addOptionalParameter("pesquisa", filter.pesquisa)
      addOptionalParameter("grade", filter.grade)
      addOptionalParameter("prdno", filter.prdno)
      addOptionalParameter("caracter", filter.caracter.value)
      addOptionalParameter("fornecedor", filter.fornecedor)
      addOptionalParameter("centroLucro", filter.centroLucro)
      addOptionalParameter("pedido", filter.pedido)
      addOptionalParameter("localizacaoUser", filter.listaUser)
      addOptionalParameter("localizacao", filter.localizacao)
      addOptionalParameter("estoque", filter.estoque.value)
      addOptionalParameter("saldo", filter.saldo)
      addOptionalParameter("inativo", filter.inativo.codigo)
      addOptionalParameter("uso", filter.uso.codigo)
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

  fun updateProdutoEstoque(produtos: List<ProdutoEstoque>) {
    transaction {
      produtos.forEach { produto ->
        updateProdutoEstoque(produto)
      }
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
      addOptionalParameter("dataInicial", produtoEstoque.dataInicial.toSaciDate())
      addOptionalParameter("dataUpdate", produtoEstoque.dataUpdate)
      addOptionalParameter("kardec", produtoEstoque.kardec)
      addOptionalParameter("dataObservacao", produtoEstoque.dataObservacao)
      addOptionalParameter("observacao", produtoEstoque.observacao)
      addOptionalParameter("estoqueUser", produtoEstoque.estoqueUser)
      addOptionalParameter("estoqueData", produtoEstoque.estoqueData)
      addOptionalParameter("estoqueCD", produtoEstoque.estoqueCD)
      addOptionalParameter("estoqueLoja", produtoEstoque.estoqueLoja)
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
      addOptionalParameter("local", filtro.localizacao)
      addOptionalParameter("datacorte", datacorte)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
      addOptionalParameter("prdno", filtro.prdno)
      addOptionalParameter("grade", filtro.grade)
      addOptionalParameter("metodo", filtro.metodo.num)
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
      addOptionalParameter("finalizadoNo", reposicao.finalizadoNo)
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
      addOptionalParameter("recebidoNo", reposicaoProduto.recebidoNo ?: 0)
      addOptionalParameter("finalizadoNo", reposicaoProduto.finalizadoNo ?: 0)
      addOptionalParameter("entregueNo", reposicaoProduto.entregueNo ?: 0)
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
      addOptionalParameter("local", filtro.localizacao)
      addOptionalParameter("prdno", filtro.prdno)
      addOptionalParameter("grade", filtro.grade)
      addOptionalParameter("tipoNota", filtro.tipoNota.codigo)
      addOptionalParameter("anexo", filtro.temAnexo.codigo)
    }
  }

  fun findNotaRecebimentoProdutoDev(
    filtro: FiltroNotaRecebimentoProdutoDev,
    situacaoDev: Int,
  ): List<NotaRecebimentoProdutoDev> {
    val sql = "/sqlSaci/findNotaRecebimentoProdutoDev.sql"
    return query(sql, NotaRecebimentoProdutoDev::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("situacaoDev", situacaoDev)
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
      addOptionalParameter("validade", notaRecebimentoProduto.validade ?: 0)
      addOptionalParameter("vencimento", notaRecebimentoProduto.vencimento)
      addOptionalParameter("usernoRecebe", notaRecebimentoProduto.usernoRecebe ?: 0)
      addOptionalParameter("selecionado", notaRecebimentoProduto.selecionado ?: false)
    }
  }

  fun listaProdutos(filtro: FiltroListaProduto): List<Produtos> {
    val sql = "/sqlSaci/listaProdutos.sql"

    val listVend = filtro.listVend.joinToString(separator = ",")

    return query(sql, Produtos::class) {
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("marca", filtro.marcaPonto.codigo)
      addOptionalParameter("inativo", filtro.inativo.codigo)
      addOptionalParameter("codigo", filtro.codigo)
      addOptionalParameter("listVend", listVend)
      addOptionalParameter("tributacao", filtro.tributacao)
      addOptionalParameter("typeno", filtro.typeno)
      addOptionalParameter("clno", filtro.clno)
      addOptionalParameter("diCompra", filtro.diCompra.toSaciDate())
      addOptionalParameter("dfCompra", filtro.dfCompra.toSaciDate())
      addOptionalParameter("diVenda", filtro.diVenda.toSaciDate())
      addOptionalParameter("dfVenda", filtro.dfVenda.toSaciDate())
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("temGrade", filtro.temGrade.let { if (it) "S" else "N" })
      addOptionalParameter("grade", filtro.grade)
      addOptionalParameter("estoque", filtro.estoque.codigo)
      addOptionalParameter("saldo", filtro.saldo)
      addOptionalParameter("validade", filtro.validade)
      addOptionalParameter("temValidade", filtro.temValidade)
    }
  }

  fun updateProduto(loja: Int, produto: Produtos) {
    val sql = "/sqlSaci/qtdVencimentoUpdate.sql"
    script(sql) {
      addOptionalParameter("storeno", loja)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
      addOptionalParameter("dataVenda", produto.dataVenda.toSaciDate())

      addOptionalParameter("qtty01", produto.qtty01)
      addOptionalParameter("venc01", produto.venc01)
      addOptionalParameter("qtty02", produto.qtty02)
      addOptionalParameter("venc02", produto.venc02)
      addOptionalParameter("qtty03", produto.qtty03)
      addOptionalParameter("venc03", produto.venc03)
      addOptionalParameter("qtty04", produto.qtty04)
      addOptionalParameter("venc04", produto.venc04)
    }
  }

  fun updateProduto(produto: NotaRecebimentoProduto) {
    val sql = "/sqlSaci/qtdVencimentoUpdate.sql"
    script(sql) {
      addOptionalParameter("storeno", produto.loja)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
      addOptionalParameter("dataVenda", produto.dataVenda.toSaciDate())

      addOptionalParameter("qtty01", produto.qtty01)
      addOptionalParameter("venc01", produto.venc01)
      addOptionalParameter("qtty02", produto.qtty02)
      addOptionalParameter("venc02", produto.venc02)
      addOptionalParameter("qtty03", produto.qtty03)
      addOptionalParameter("venc03", produto.venc03)
      addOptionalParameter("qtty04", produto.qtty04)
      addOptionalParameter("venc04", produto.venc04)
    }
  }

  /*
    fun updateProduto(produto: NotaRecebimentoProdutoDev) {
      val sql = "/sqlSaci/qtdVencimentoUpdate.sql"
      script(sql) {
        addOptionalParameter("storeno", produto.loja)
        addOptionalParameter("prdno", produto.prdno)
        addOptionalParameter("grade", produto.grade)
        addOptionalParameter("dataVenda", produto.dataVenda.toSaciDate())

        addOptionalParameter("qtty01", produto.qtty01)
        addOptionalParameter("venc01", produto.venc01)
        addOptionalParameter("qtty02", produto.qtty02)
        addOptionalParameter("venc02", produto.venc02)
        addOptionalParameter("qtty03", produto.qtty03)
        addOptionalParameter("venc03", produto.venc03)
        addOptionalParameter("qtty04", produto.qtty04)
        addOptionalParameter("venc04", produto.venc04)
      }
    }
  */
  fun consultaValidade(filtro: FiltroValidade): List<ComparaValidade> {
    val sql = "/sqlSaci/consultaValidade.sql"

    val listVend = filtro.listVend.joinToString(separator = ",")

    return query(sql, ComparaValidade::class) {
      addOptionalParameter("tipo", filtro.tipo.num)
      addOptionalParameter("query", filtro.query)
      addOptionalParameter("marca", filtro.marca.codigo)

      addOptionalParameter("listVend", listVend)
      addOptionalParameter("tributacao", filtro.tributacao)
      addOptionalParameter("typeno", filtro.typeno)
      addOptionalParameter("clno", filtro.clno)
    }
  }

  fun findFornecedores(): List<Fornecedor> {
    val sql = "/sqlSaci/fornecedores.sql"
    return query(sql, Fornecedor::class)
  }

  fun findPrdNfe(numero: String): List<PrdCodigo> {
    val sql = "/sqlSaci/findPrdNfe.sql"

    return query(sql, PrdCodigo::class) {
      if (numero.contains("/")) {
        val nfno = numero.split("/").getOrNull(0) ?: ""
        val nfse = numero.split("/").getOrNull(1) ?: ""
        val invno = 0
        addOptionalParameter("nfno", nfno)
        addOptionalParameter("nfse", nfse)
        addOptionalParameter("invno", invno)
      } else {
        val nfno = ""
        val nfse = ""
        val invno = numero.toIntOrNull() ?: 0
        addOptionalParameter("nfno", nfno)
        addOptionalParameter("nfse", nfse)
        addOptionalParameter("invno", invno)
      }
    }
  }

  fun saldoData(diDate: LocalDate?, dfDate: LocalDate?): List<SaldoVenda> {
    val sql = "/sqlSaci/totalVendas.sql"

    return query(sql, SaldoVenda::class) {
      addOptionalParameter("diVenda", diDate.toSaciDate())
      addOptionalParameter("dfVenda", dfDate.toSaciDate())
    }
  }

  fun listValidade(): List<Validade> {
    val sql = "/sqlSaci/validadeList.sql"
    return query(sql, Validade::class)
  }

  fun saveValidade(bean: Validade) {
    val sql = "/sqlSaci/validadeSave.sql"
    script(sql) {
      addOptionalParameter("validade", bean.validade)
      addOptionalParameter("mesesFabricacao", bean.mesesFabricacao)
    }
  }

  fun delValidade(bean: Validade) {
    val sql = "/sqlSaci/validadeDel.sql"
    script(sql) {
      addOptionalParameter("validade", bean.validade)
    }
  }

  fun produtoValidade(filtro: FiltroProdutoInventario): List<ProdutoInventario> {
    val sql = "/sqlSaci/produtoValidade.sql"
    val ano = when {
      filtro.ano == 0   -> 0
      filtro.ano < 2000 -> filtro.ano + 2000
      else              -> filtro.ano
    }
    return query(sql, ProdutoInventario::class) {
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("codigo", filtro.codigo)
      addOptionalParameter("validade", filtro.validade)
      addOptionalParameter("mes", filtro.mes)
      addOptionalParameter("ano", ano)
      addOptionalParameter("loja", 0)
      addOptionalParameter("grade", filtro.grade)
      addOptionalParameter("caracter", filtro.caracter.value)
    }
  }

  fun updateProdutoValidade(produtoInventario: ProdutoInventario) {
    val sql = "/sqlSaci/produtoValidadeUpdate.sql"

    script(sql) {
      addOptionalParameter("storeno", produtoInventario.loja ?: 0)
      addOptionalParameter("prdno", produtoInventario.prdno)
      addOptionalParameter("grade", produtoInventario.grade)
      addOptionalParameter("dataEntrada", produtoInventario.dataEntrada.toSaciDate())
      addOptionalParameter("dataEntradaEdit", produtoInventario.dataEntradaEdit.toSaciDate())
      addOptionalParameter("vencimento", produtoInventario.vencimento ?: 0)
      addOptionalParameter("vencimentoEdit", produtoInventario.vencimentoEdit ?: 0)
      addOptionalParameter("tipo", produtoInventario.tipo ?: "")
      addOptionalParameter("tipoEdit", produtoInventario.tipoEdit ?: "")
      addOptionalParameter("movimento", produtoInventario.movimento ?: 0)
    }
  }

  fun removeProdutoValidade(produtoInventario: ProdutoInventario) {
    val sql = "/sqlSaci/produtoValidadeRemove.sql"
    script(sql) {
      addOptionalParameter("storeno", produtoInventario.loja ?: 0)
      addOptionalParameter("prdno", produtoInventario.prdno)
      addOptionalParameter("grade", produtoInventario.grade)
      addOptionalParameter("vencimento", produtoInventario.vencimentoEdit ?: 0)
      addOptionalParameter("tipo", produtoInventario.tipoEdit ?: "")
      addOptionalParameter("dataEntrada", produtoInventario.dataEntrada.toSaciDate())
    }
  }

  fun produtoValidadeSaida(filtro: FiltroProdutoInventario, dataInicial: LocalDate?): List<ProdutoSaida> {
    val sql = "/sqlSaci/produtoValidadeSaida.sql"
    val ano = when {
      filtro.ano == 0   -> 0
      filtro.ano < 2000 -> filtro.ano + 2000
      else              -> filtro.ano
    }
    return query(sql, ProdutoSaida::class) {
      this.addOptionalParameter("dataInicial", dataInicial.toSaciDate())
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("codigo", filtro.codigo)
      addOptionalParameter("validade", filtro.validade)
      addOptionalParameter("mes", filtro.mes)
      addOptionalParameter("ano", ano)
      addOptionalParameter("grade", filtro.grade)
      addOptionalParameter("caracter", filtro.caracter.value)
    }
  }

  fun produtoValidadeEntrada(filtro: FiltroProdutoInventario, dataInicial: LocalDate?): List<ProdutoRecebimento> {
    val sql = "/sqlSaci/produtoValidadeEntrada.sql"
    val ano = when {
      filtro.ano == 0   -> 0
      filtro.ano < 2000 -> filtro.ano + 2000
      else              -> filtro.ano
    }
    return query(sql, ProdutoRecebimento::class) {
      this.addOptionalParameter("dataInicial", dataInicial.toSaciDate())
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("codigo", filtro.codigo)
      addOptionalParameter("validade", filtro.validade)
      addOptionalParameter("mes", filtro.mes)
      addOptionalParameter("ano", ano)
      addOptionalParameter("loja", 0)
      addOptionalParameter("grade", filtro.grade)
      addOptionalParameter("caracter", filtro.caracter.value)
    }
  }

  fun findInvFile(invno: Int): List<InvFile> {
    val sql = "/sqlSaci/invArquivo.sql"
    return query(sql, InvFile::class) {
      addOptionalParameter("invno", invno)
    }
  }

  fun findInvFile(invno: Int, tipo: ETipoDevolucao, numero: Int): List<InvFileDev> {
    val sql = "/sqlSaci/invArquivoDev.sql"
    return query(sql, InvFileDev::class) {
      addOptionalParameter("invno", invno)
      addOptionalParameter("tipo", tipo.num)
      addOptionalParameter("numero", numero)
    }
  }

  fun updateInvFile(file: InvFile) {
    val sql = "/sqlSaci/invArquivoUpdate.sql"
    script(sql) {
      addOptionalParameter("invno", file.invno ?: 0)
      addOptionalParameter("title", file.title)
      addOptionalParameter("file", file.file)
      addOptionalParameter("date", file.date.toSaciDate())
      addOptionalParameter("filename", file.fileName)
    }
  }

  fun updateInvFile(file: InvFileDev) {
    val sql = "/sqlSaci/invArquivoUpdateDev.sql"
    script(sql) {
      addOptionalParameter("invno", file.invno ?: 0)
      addOptionalParameter("numero", file.numero ?: 0)
      addOptionalParameter("tipoDevolucao", file.tipoDevolucao ?: 0)
      addOptionalParameter("date", file.date.toSaciDate())
      addOptionalParameter("filename", file.fileName ?: "")
      addOptionalParameter("file", file.file)
    }
  }

  fun deleteInvFile(file: InvFile) {
    val sql = "/sqlSaci/invArquivoDelete.sql"
    script(sql) {
      addOptionalParameter("seq", file.seq ?: 0)
    }
  }

  fun deleteInvFile(file: InvFileDev) {
    val sql = "/sqlSaci/invArquivoDeleteDev.sql"
    script(sql) {
      addOptionalParameter("seq", file.seq ?: 0)
    }
  }

  fun atualizarTabelas() {
    updateProdutoValidadeSaida()
    updateProdutoValidadeRecebimento()
  }

  fun updateProdutoValidadeRecebimento() {
    val sql = "/sqlSaci/produtoValidadeEntradaTable.sql"
    script(sql) {
      addOptionalParameter("dataInicial", 20240101)
    }
  }

  fun updateProdutoValidadeSaida() {
    val sql = "/sqlSaci/produtoValidadeSaidaTable.sql"
    script(sql) {
      addOptionalParameter("dataInicial", 20240101)
    }

  }

  fun dadosValidade(filtro: FiltroDadosValidade): List<DadosValidade> {
    val sql = "/sqlSaci/dadosValidade.sql"
    val ano = when {
      filtro.ano == 0   -> 0
      filtro.ano < 2000 -> filtro.ano + 2000
      else              -> filtro.ano
    }
    return query(sql, DadosValidade::class) {
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("codigo", filtro.codigo)
      addOptionalParameter("validade", filtro.validade)
      addOptionalParameter("mes", filtro.mes)
      addOptionalParameter("ano", ano)
      addOptionalParameter("loja", filtro.storeno)
      addOptionalParameter("grade", filtro.grade)
      addOptionalParameter("caracter", filtro.caracter.value)
    }
  }

  fun dadosValidadeInsert(loja: Int, codigo: String, grade: String): Int {
    val sql = "/sqlSaci/dadosValidadeInsert.sql"
    return query(sql, Count::class) {
      addOptionalParameter("loja", loja)
      addOptionalParameter("codigo", codigo)
      addOptionalParameter("grade", grade)
    }.firstOrNull()?.quant ?: 0
  }

  fun dadosValidadeUpdate(dadosValidade: DadosValidade) {
    val sql = "/sqlSaci/dadosValidadeUpdate.sql"
    script(sql) {
      addOptionalParameter("seq", dadosValidade.seq)
      addOptionalParameter("storeno", dadosValidade.loja)
      addOptionalParameter("prdno", dadosValidade.prdno)
      addOptionalParameter("grade", dadosValidade.grade)
      addOptionalParameter("vencimento", dadosValidade.vencimento)
      addOptionalParameter("inventario", dadosValidade.inventario)
      addOptionalParameter("dataEntrada", dadosValidade.dataEntrada.toSaciDate())
    }
  }

  fun dadosValidadeDelete(dadosValidade: DadosValidade) {
    val sql = "/sqlSaci/dadosValidadeDelete.sql"
    script(sql) {
      addOptionalParameter("seq", dadosValidade.seq)
    }
  }

  fun selectCliente(filtro: FiltroDadosCliente): List<DadosCliente> {
    val sql = "/sqlSaci/selectClientes.sql"
    return query(sql, DadosCliente::class) {
      this.addOptionalParameter("pesquisa", filtro.pesquisa)
    }
  }

  fun updateValidadeSaci(validade: ValidadeSaci) {
    val sql = "/sqlSaci/updateValidadeSaci.sql"
    script(sql) {
      addOptionalParameter("tipoValidade", validade.tipoValidade ?: 0)
      addOptionalParameter("tempoValidade", validade.tempoValidade ?: 0)
      addOptionalParameter("prdno", validade.prdno ?: "")
    }
  }

  fun findSaldoData(loja: Int, codigo: String, grade: String, dataInicial: LocalDate): List<SaldoData> {
    val sql = "/sqlSaci/saldoData.sql"
    return query(sql, SaldoData::class) {
      addOptionalParameter("loja", loja)
      addOptionalParameter("codigo", codigo)
      addOptionalParameter("grade", grade)
      addOptionalParameter("dataInicial", dataInicial.toSaciDate())
    }
  }

  fun findAcertoEstoque(loja: Int, codigo: String, grade: String, dataInicial: LocalDate): List<AcertoEstoque> {
    val sql = "/sqlSaci/acertoEstoque.sql"
    return query(sql, AcertoEstoque::class) {
      addOptionalParameter("loja", loja)
      addOptionalParameter("codigo", codigo)
      addOptionalParameter("grade", grade)
      addOptionalParameter("dataInicial", dataInicial.toSaciDate())
    }
  }

  fun listaAgenda(filtro: FiltroAgenda): List<Agenda> {
    val sql = "/sqlSaci/listaAgenda.sql"
    return query(sql, Agenda::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("filtro", filtro.pesquisa)
    }
  }

  fun findPedidoProduto(filtro: FiltroPedidoNota): List<PedidoProduto> {
    val sql = "/sqlSaci/findPedidos.sql"
    return query(sql, PedidoProduto::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("status", filtro.status.cod)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
    }
  }

  fun findPedidoProdutoCompra(storeno: Int, pedido: Int): List<PedidoProdutoCompra> {
    val sql = "/sqlSaci/findPedidoProduto.sql"
    return query(sql, PedidoProdutoCompra::class) {
      addOptionalParameter("loja", storeno)
      addOptionalParameter("pedido", pedido)
    }
  }

  fun findProdutoCadastro(filtro: FiltroProdutoCadastro): List<ProdutoCadastro> {
    val sql = "/sqlSaci/produtoCadastro.sql"
    return query(sql, ProdutoCadastro::class) {
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("vendno", filtro.vendno)
      addOptionalParameter("taxno", filtro.taxno)
      addOptionalParameter("typeno", filtro.typeno)
      addOptionalParameter("clno", filtro.clno)
      addOptionalParameter("rotulo", filtro.rotulo)
      addOptionalParameter("caracter", filtro.caracter.value)
      addOptionalParameter("letraDup", filtro.letraDup.value)
    }
  }

  fun findProdutoSped(filtro: FiltroProdutoSped): List<ProdutoSped> {
    val sql = "/sqlSaci/produtoSped.sql"
    return query(sql, ProdutoSped::class) {
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("vendno", filtro.vendno)
      addOptionalParameter("taxno", filtro.taxno)
      addOptionalParameter("typeno", filtro.typeno)
      addOptionalParameter("clno", filtro.clno)
      addOptionalParameter("rotulo", filtro.rotulo)
      addOptionalParameter("caracter", filtro.caracter.value)
      addOptionalParameter("letraDup", filtro.letraDup.value)
      addOptionalParameter("consumo", filtro.consumo.value)
      addOptionalParameter("configSt", filtro.configSt.let { if (it) "S" else "N" })
      addOptionalParameter("pisCofN", filtro.pisCofN.let { if (it) "S" else "N" })
      addOptionalParameter("rotuloN", filtro.rotuloN.let { if (it) "S" else "N" })
    }
  }

  fun updateProdutoSt(prdno: String) {
    val sql = "/sqlSaci/updateProdutoSt.sql"
    script(sql) {
      addOptionalParameter("prdno", prdno)
    }
  }

  fun listNFEntrada(filter: FiltroNotaEntradaXML): List<NotaEntradaXML> {
    val sql = "/sqlSaci/listNFEntrada.sql"
    return query(sql, NotaEntradaXML::class) {
      addOptionalParameter("loja", filter.loja)
      addOptionalParameter("dataInicial", filter.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filter.dataFinal.toSaciDate())
      addOptionalParameter("numero", filter.numero)
      addOptionalParameter("cnpj", filter.cnpj)
      addOptionalParameter("fornecedor", filter.fornecedor)
      addOptionalParameter("preEntrada", filter.preEntrada.codigo)
      addOptionalParameter("entrada", filter.entrada.codigo)
    }.toList()
  }

  fun listPedidoXml(loja: Int, pedido: Int): List<PedidoXML> {
    val sql = "/sqlSaci/pedidoXml.sql"
    return query(sql, PedidoXML::class) {
      addOptionalParameter("loja", loja)
      addOptionalParameter("pedido", pedido)
    }
  }

  fun listPedidoXmlSave(pedido: PedidoXML) {
    val sql = "/sqlSaci/pedidoXmlSave.sql"
    script(sql) {
      addOptionalParameter("storeno", pedido.loja)
      addOptionalParameter("ordno", pedido.pedido)
      addOptionalParameter("prdno", pedido.prdno)
      addOptionalParameter("grade", pedido.grade)
      addOptionalParameter("quantFat", pedido.quantFat ?: 0)
    }
  }

  fun saveNFEntrada(nota: NotaEntradaXML) {
    val sql = "/sqlSaci/listNFEntradaSave.sql"
    script(sql) {
      addOptionalParameter("id", nota.id)
      addOptionalParameter("loja", nota.loja)
      addOptionalParameter("pedido", nota.pedidoEdit ?: 0)
    }
  }

  fun proximoNumeroInvno2(): Int {
    val sql = "/sqlSaci/proximoNumeroInvno2.sql"
    return query(sql, Count::class).firstOrNull()?.quant ?: 0
  }

  fun findCarr(doc: String): Int {
    val sql = "/sqlSaci/findCarr.sql"
    return query(sql, Count::class) {
      addOptionalParameter("doc", doc)
    }.firstOrNull()?.quant ?: 0
  }

  fun processaEntrada(parameters: Inv2Parameters) {
    val sql = "/sqlSaci/insertInv2.sql"
    script(sql) {
      this.bind(parameters)
    }
  }

  fun processaItensEntrada(parameters: Iprd2Parameters) {
    val sql = "/sqlSaci/insertIPrd2.sql"
    script(sql) {
      this.bind(parameters)
    }
  }

  fun findPedidosRessuprimento(filtro: FiltroPedidoRessuprimento): List<PedidoRessuprimento> {
    val sql = "/sqlSaci/findPedidosRessuprimento.sql"
    return query(sql, PedidoRessuprimento::class) {
      addOptionalParameter("pesquisa", filtro.pesquisa)
    }
  }

  fun duplicaPedido(pedido: PedidoRessuprimento): PedidoNovo? {
    val sql = "/sqlSaci/duplicaPedido.sql"
    return query(sql, PedidoNovo::class) {
      addOptionalParameter("ordno", pedido.pedido ?: 0)
    }.firstOrNull()
  }

  fun separaPedido(produto: ProdutoRessuprimento, ordnoNovo: Int): PedidoNovo? {
    val sql = "/sqlSaci/separaPedido.sql"
    return query(sql, PedidoNovo::class) {
      addOptionalParameter("ordno", produto.ordno ?: 0)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
      addOptionalParameter("qtty", produto.qtPedido ?: 0)
      addOptionalParameter("localizacao", produto.localizacao)
      addOptionalParameter("ordnoNovo", ordnoNovo)
    }.firstOrNull()
  }

  fun pedidoNovo(ordno: Int): PedidoNovo? {
    val sql = "/sqlSaci/pedidoNovo.sql"
    return query(sql, PedidoNovo::class) {
      addOptionalParameter("ordno", ordno)
    }.firstOrNull()
  }

  fun removerPedido(ordno: Int) {
    val sql = "/sqlSaci/removerPedido.sql"
    script(sql) {
      addOptionalParameter("ordno", ordno)
    }
  }

  fun removerProduto(produto: ProdutoRessuprimento) {
    val sql = "/sqlSaci/removerProduto.sql"
    script(sql) {
      addOptionalParameter("ordno", produto.ordno ?: 0)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
    }
  }

  fun salvaQuantidadeProduto(produto: ProdutoRessuprimento) {
    val sql = "/sqlSaci/updateQuantidade.sql"
    script(sql) {
      addOptionalParameter("ordno", produto.ordno ?: 0)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
      addOptionalParameter("qtty", produto.qtPedido ?: 0)
    }
  }

  fun localizacaoAlternativa(): List<LocalizacaoAlternativa> {
    val sql = "/sqlSaci/localizacaoAlternativa.sql"
    return query(sql, LocalizacaoAlternativa::class)
  }

  fun findPedidosAcerto(): List<PedidoAcerto> {
    val sql = "/sqlSaci/findPedidosAcerto.sql"
    return query(sql, PedidoAcerto::class)
  }

  fun findProdutoAcerto(pedido: PedidoAcerto, lojaAcerto: Int): List<ProdutoAcerto> {
    val sql = "/sqlSaci/findPedidoProdutosAcerto.sql"

    return query(sql, ProdutoAcerto::class) {
      addOptionalParameter("loja", pedido.loja ?: 0)
      addOptionalParameter("pedido", pedido.pedido ?: 0)
      addOptionalParameter("lojaAcerto", lojaAcerto)
    }
  }

  fun removeProdutoAcerto(acerto: ProdutoAcerto) {
    val sql = "/sqlSaci/removeProdutoAcerto.sql"
    script(sql) {
      addOptionalParameter("loja", acerto.loja ?: 0)
      addOptionalParameter("pedido", acerto.pedido ?: 0)
      addOptionalParameter("prdno", acerto.prdno)
      addOptionalParameter("grade", acerto.grade)
    }
  }

  fun listaPrecificacao(filtro: FiltroPrecificacao): List<Precificacao> {
    val sql = "/sqlSaci/selectPrecificacao.sql"

    val listVend = filtro.listVend.joinToString(separator = ",")

    return query(sql, Precificacao::class) {
      addOptionalParameter("codigo", filtro.codigo)
      addOptionalParameter("listVend", listVend)
      addOptionalParameter("tributacao", filtro.tributacao)
      addOptionalParameter("mva", filtro.mva)
      addOptionalParameter("typeno", filtro.typeno)
      addOptionalParameter("clno", filtro.clno)
      addOptionalParameter("marca", filtro.marcaPonto.codigo)
      addOptionalParameter("query", filtro.query)
    }
  }

  fun savePrecificacao(prp: Precificacao) {
    val sql = "/sqlSaci/updatePrecificacao.sql"
    script(sql) {
      addOptionalParameter("prdno", prp.prdno)

      addOptionalParameter("mvap", prp.mvap)
      addOptionalParameter("creditoICMS", prp.creditoICMS)
      addOptionalParameter("pcfabrica", prp.pcfabrica)
      addOptionalParameter("ipi", prp.ipi)

      addOptionalParameter("embalagem", prp.embalagem)
      addOptionalParameter("retido", prp.retido)
      addOptionalParameter("icmsp", prp.icmsp)
      addOptionalParameter("frete", prp.frete)
      addOptionalParameter("freteICMS", prp.freteICMS)

      addOptionalParameter("icms", prp.icms)
      addOptionalParameter("fcp", prp.fcp)
      addOptionalParameter("pis", prp.pis)
      addOptionalParameter("ir", prp.ir)

      addOptionalParameter("contrib", prp.contrib)
      addOptionalParameter("cpmf", prp.cpmf)
      addOptionalParameter("fixa", prp.fixa)
      addOptionalParameter("outras", prp.outras)
    }
  }

  fun saveListPrecificacao(list: List<Precificacao>, bean: BeanForm) {
    transaction {
      list.forEach { pre ->
        val mvap = bean.mvap
        if (mvap != null) {
          pre.mvap = mvap.toDouble()
        }

        val creditoICMS = bean.creditoICMS
        if (creditoICMS != null) {
          pre.creditoICMS = creditoICMS.toDouble()
        }

        val pcfabrica = bean.pcfabrica
        if (pcfabrica != null) {
          pre.pcfabrica = pcfabrica.toDouble()
        }

        val ipi = bean.ipi
        if (ipi != null) {
          pre.ipi = ipi.toDouble()
        }

        val embalagem = bean.embalagem
        if (embalagem != null) {
          pre.embalagem = embalagem.toDouble()
        }

        val retido = bean.retido
        if (retido != null) {
          pre.retido = retido.toDouble()
        }

        val icmsp = bean.icmsp
        if (icmsp != null) {
          pre.icmsp = icmsp.toDouble()
        }

        val frete = bean.frete
        if (frete != null) {
          pre.frete = frete.toDouble()
        }

        val freteICMS = bean.freteICMS
        if (freteICMS != null) {
          pre.freteICMS = freteICMS.toDouble()
        }

        val icms = bean.icms
        if (icms != null) {
          pre.icms = icms.toDouble()
        }

        val fcp = bean.fcp
        if (fcp != null) {
          pre.fcp = fcp.toDouble()
        }

        val pis = bean.pis
        if (pis != null) {
          pre.pis = pis.toDouble()
        }

        val ir = bean.ir
        if (ir != null) {
          pre.ir = ir.toDouble()
        }

        val contrib = bean.contrib
        if (contrib != null) {
          pre.contrib = contrib.toDouble()
        }

        val cpmf = bean.cpmf
        if (cpmf != null) {
          pre.cpmf = cpmf.toDouble()
        }

        val fixa = bean.fixa
        if (fixa != null) {
          pre.fixa = fixa.toDouble()
        }

        val outras = bean.outras
        if (outras != null) {
          pre.outras = outras.toDouble()
        }

        pre.save()
      }
    }
  }

  fun qtdVencimento(): List<QtdVencimento> {
    val sql = "/sqlSaci/qtdVencimento.sql"
    return query(sql, QtdVencimento::class)
  }

  fun processaVendas(storno: Int = 0, prdno: String = "", grade: String = "") {
    val sql = "/sqlSaci/qtdVencimentoVendas.sql"
    script(sql) {
      addOptionalParameter("storeno", storno)
      addOptionalParameter("prdno", prdno)
      addOptionalParameter("grade", grade)
    }
  }

  fun deleteKardec(produto: ProdutoEstoque) {
    val sql = "/sqlSaci/kardecDelete.sql"
    script(sql) {
      addOptionalParameter("loja", produto.loja)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
    }
  }

  fun saveKardec(produtoKardec: ProdutoKardec) {
    val sql = "/sqlSaci/kardecSave.sql"
    script(sql) {
      addOptionalParameter("loja", produtoKardec.loja)
      addOptionalParameter("prdno", produtoKardec.prdno)
      addOptionalParameter("grade", produtoKardec.grade)
      addOptionalParameter("data", produtoKardec.data.toSaciDate())
      addOptionalParameter("doc", produtoKardec.doc)
      addOptionalParameter("tipo", produtoKardec.tipo?.name)
      addOptionalParameter("vencimento", produtoKardec.vencimento.toSaciDate())
      addOptionalParameter("qtde", produtoKardec.qtde)
      addOptionalParameter("saldo", produtoKardec.saldo)
      addOptionalParameter("userLogin", produtoKardec.userLogin)
    }
  }

  fun selectKardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    val sql = "/sqlSaci/kardecSelect.sql"
    return query(sql, ProdutoKardec::class) {
      addOptionalParameter("loja", produto.loja)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
    }
  }

  fun acertoNovo(numero: Int, numLoja: Int): ProdutoEstoqueAcerto? {
    val sql = "/sqlSaci/produtoEstoqueAcertoNovo.sql"

    val user = AppConfig.userLogin() as? UserSaci

    return query(sql, ProdutoEstoqueAcerto::class) {
      addOptionalParameter("numLoja", numLoja)
      addOptionalParameter("numero", numero)
      addOptionalParameter("login", user?.login ?: "")
      addOptionalParameter("usuario", user?.name ?: "")
      addOptionalParameter("data", LocalDate.now().toSaciDate())
      addOptionalParameter("hora", LocalTime.now().toSecondOfDay())
    }.firstOrNull()
  }

  fun garantiaNovo(numero: Int, numLoja: Int): ProdutoPedidoGarantia? {
    val sql = "/sqlSaci/produtoEstoqueGarantiaNovo.sql"

    val user = AppConfig.userLogin() as? UserSaci

    return query(sql, ProdutoPedidoGarantia::class) {
      addOptionalParameter("numLoja", numLoja)
      addOptionalParameter("numero", numero)
      addOptionalParameter("login", user?.login ?: "")
      addOptionalParameter("usuario", user?.name ?: "")
      addOptionalParameter("data", LocalDate.now().toSaciDate())
      addOptionalParameter("hora", LocalTime.now().toSecondOfDay())
    }.firstOrNull()
  }

  fun acertoProximo(numLoja: Int): Int {
    val sql = "/sqlSaci/produtoEstoqueAcertoProximo.sql"
    return query(sql, Count::class) {
      addOptionalParameter("numLoja", numLoja)
    }.firstOrNull()?.quant ?: 1
  }

  fun garantiaProximo(numLoja: Int): Int {
    val sql = "/sqlSaci/produtoEstoqueGarantiaProximo.sql"
    return query(sql, Count::class) {
      addOptionalParameter("numLoja", numLoja)
    }.firstOrNull()?.quant ?: 1
  }

  fun acertoUpdate(produto: ProdutoEstoqueAcerto) {
    val sql = "/sqlSaci/produtoEstoqueAcertoUpdate.sql"
    script(sql) {
      addOptionalParameter("numero", produto.numero)
      addOptionalParameter("numloja", produto.numloja)
      addOptionalParameter("data", produto.data.toSaciDate())
      addOptionalParameter("hora", produto.hora)
      addOptionalParameter("login", produto.login)
      addOptionalParameter("usuario", produto.usuario)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
      addOptionalParameter("estoqueSis", produto.estoqueSis)
      addOptionalParameter("estoqueCD", produto.estoqueCD)
      addOptionalParameter("estoqueLoja", produto.estoqueLoja)
      addOptionalParameter("gravadoLogin", produto.gravadoLogin ?: 0)
      addOptionalParameter("gravado", produto.gravado ?: 0)
    }
  }

  fun garantiaUpdate(produto: ProdutoPedidoGarantia) {
    val sql = "/sqlSaci/produtoEstoqueGarantiaUpdate.sql"
    script(sql) {
      addOptionalParameter("numero", produto.numero)
      addOptionalParameter("numloja", produto.numloja)
      addOptionalParameter("data", produto.data.toSaciDate())
      addOptionalParameter("hora", produto.hora)
      addOptionalParameter("usuario", produto.usuario)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
      addOptionalParameter("estoqueSis", produto.estoqueLoja)
      addOptionalParameter("estoqueReal", produto.estoqueDev)
      addOptionalParameter("loteDev", produto.loteDev)
    }
  }

  fun acertoFindAll(filtro: FiltroAcerto): List<ProdutoEstoqueAcerto> {
    val sql = "/sqlSaci/produtoEstoqueAcertoFindAll.sql"
    return query(sql, ProdutoEstoqueAcerto::class) {
      addOptionalParameter("numLoja", filtro.numLoja)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
      addOptionalParameter("numero", filtro.numero)
    }
  }

  fun garantiaFindAll(filtro: FiltroGarantia): List<ProdutoPedidoGarantia> {
    val sql = "/sqlSaci/produtoEstoqueGarantiaFindAll.sql"
    return query(sql, ProdutoPedidoGarantia::class) {
      addOptionalParameter("numLoja", filtro.numLoja)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
      addOptionalParameter("numero", filtro.numero)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("processado", filtro.processado)
    }
  }

  fun jaGravado(produtoEstoqueAcerto: ProdutoEstoqueAcerto): Boolean {
    val sql = "/sqlSaci/produtoEstoqueAcertoJaGravado.sql"
    return (query(sql, Count::class) {
      addOptionalParameter("numLoja", produtoEstoqueAcerto.numloja)
      addOptionalParameter("data", produtoEstoqueAcerto.data.toSaciDate())
      addOptionalParameter("prdno", produtoEstoqueAcerto.prdno)
      addOptionalParameter("grade", produtoEstoqueAcerto.grade)
    }.firstOrNull()?.quant ?: 0) > 0
  }

  fun jaGravadoGarantia(produtoPedidoGarantia: ProdutoPedidoGarantia): Boolean {
    val sql = "/sqlSaci/produtoEstoqueGarantiaJaGravado.sql"
    return (query(sql, Count::class) {
      addOptionalParameter("numLoja", produtoPedidoGarantia.numloja)
      addOptionalParameter("data", produtoPedidoGarantia.data.toSaciDate())
      addOptionalParameter("prdno", produtoPedidoGarantia.prdno)
      addOptionalParameter("grade", produtoPedidoGarantia.grade)
    }.firstOrNull()?.quant ?: 0) > 0
  }

  fun acertoCancela(produtoEstoqueAcerto: EstoqueAcerto) {
    val sql = "/sqlSaci/produtoEstoqueAcertoCancela.sql"
    script(sql) {
      addOptionalParameter("numloja", produtoEstoqueAcerto.numloja)
      addOptionalParameter("numero", produtoEstoqueAcerto.numero)
    }
  }

  fun garantiaCancela(produtoEstoqueAcerto: PedidoGarantia) {
    val sql = "/sqlSaci/produtoEstoqueAcertoCancela.sql"
    script(sql) {
      addOptionalParameter("numloja", produtoEstoqueAcerto.numloja)
      addOptionalParameter("numero", produtoEstoqueAcerto.numero)
    }
  }

  fun removeAcertoProduto(produtoEstoque: ProdutoEstoque) {
    val sql = "/sqlSaci/produtoEstoqueAcertoLimpa.sql"
    script(sql) {
      addOptionalParameter("numLoja", produtoEstoque.loja)
      addOptionalParameter("numero", produtoEstoque.numeroAcerto)
      addOptionalParameter("prdno", produtoEstoque.prdno)
      addOptionalParameter("grade", produtoEstoque.grade)
    }
  }

  fun removeGarantiaProduto(produtoEstoque: ProdutoPedidoGarantia) {
    val sql = "/sqlSaci/produtoEstoqueGarantiaLimpa.sql"
    script(sql) {
      addOptionalParameter("numLoja", produtoEstoque.numloja)
      addOptionalParameter("numero", produtoEstoque.numero)
      addOptionalParameter("prdno", produtoEstoque.prdno)
      addOptionalParameter("grade", produtoEstoque.grade)
    }
  }

  fun removeAcertoProduto(produtoEstoque: ProdutoEstoqueAcerto) {
    val sql = "/sqlSaci/produtoEstoqueAcertoLimpa.sql"
    script(sql) {
      addOptionalParameter("numLoja", produtoEstoque.numloja)
      addOptionalParameter("numero", produtoEstoque.numero)
      addOptionalParameter("prdno", produtoEstoque.prdno)
      addOptionalParameter("grade", produtoEstoque.grade)
    }
  }

  fun copiaPedido(beanCopia: BeanCopia) {
    val sql = "/sqlSaci/copiaPedido.sql"
    script(sql) {
      addOptionalParameter("lojaOriginal", beanCopia.lojaOriginal)
      addOptionalParameter("pedidoOriginal", beanCopia.numPedidoOriginal)
      addOptionalParameter("lojaDestino", beanCopia.lojaDestino)
      addOptionalParameter("pedidoDestino", beanCopia.numPedidoDestino)
    }
  }

  fun updateAcerto(acertoEstoque: EstoqueAcerto) {
    val sql = "/sqlSaci/produtoObservacaoAcertoUpdate.sql"
    script(sql) {
      addOptionalParameter("numloja", acertoEstoque.numloja)
      addOptionalParameter("numero", acertoEstoque.numero)
      addOptionalParameter("observacao", acertoEstoque.observacao)
    }
  }

  fun updateGarantia(garantiaEstoque: PedidoGarantia) {
    val sql = "/sqlSaci/produtoObservacaoGarantiaUpdate.sql"
    script(sql) {
      addOptionalParameter("numloja", garantiaEstoque.numloja)
      addOptionalParameter("numero", garantiaEstoque.numero)
      addOptionalParameter("observacao", garantiaEstoque.observacao)
      addOptionalParameter("dataNfdGarantia", garantiaEstoque.dataNfdGarantia.toSaciDate())
      addOptionalParameter("nfdGarantia", garantiaEstoque.nfdGarantia)
    }
  }

  fun saveTipoDevolucao(produto: NotaRecebimentoProduto, tipo: ETipoDevolucao, numero: Int) {
    val sql = "/sqlSaci/saveTipoDevolucao.sql"
    script(sql) {
      addOptionalParameter("invno", produto.ni)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
      addOptionalParameter("numero", numero)
      addOptionalParameter("tipoDevolucao", tipo.num)
      addOptionalParameter("quantDevolucao", produto.quantDevolucao ?: 0)
    }
  }

  fun saveTipoDevolucao(produto: ProdutoPedidoGarantia) {
    val sql = "/sqlSaci/updateTipoDevolucaoGarantia.sql"
    if (produto.niReceb == null)
      return
    script(sql) {
      addOptionalParameter("invno", produto.niReceb)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
      addOptionalParameter("numero", produto.numero)
      addOptionalParameter("situacaoDev", EStituacaoDev.GARANTIA.num)
      addOptionalParameter("tipoDevolucao", ETipoDevolucao.EM_GARANTIA.num)
      addOptionalParameter("quantDevolucao", produto.estoqueDev ?: 0)
    }
  }

  fun saveInvAdicional(nota: NotaRecebimentoDev, userno: Int) {
    val sql = "/sqlSaci/invAdicionalSave.sql"
    nota.niList.forEach { invno ->
      script(sql) {
        addOptionalParameter("invno", invno)
        addOptionalParameter("numero", nota.numeroDevolucao)
        addOptionalParameter("tipoDevolucao", nota.tipoDevolucao)
        addOptionalParameter("volume", nota.volumeDevolucao)
        addOptionalParameter("peso", nota.pesoDevolucao)
        addOptionalParameter("transp", nota.transpDevolucao)
        addOptionalParameter("cte", nota.cteDevolucao)
        addOptionalParameter("data", nota.dataDevolucao.toSaciDate())
        addOptionalParameter("dataColeta", nota.dataColeta.toSaciDate())
        addOptionalParameter("situacaoDev", nota.situacaoDev)
        addOptionalParameter("observacaoDev", nota.observacaoDev)
        addOptionalParameter("observacaoAdicional", nota.observacaoAdicional)
        addOptionalParameter("userno", userno)
      }
    }
  }

  fun findTransportadora(carrno: Int): Transportadora? {
    val sql = "/sqlSaci/transportadora.sql"
    return query(sql, Transportadora::class) {
      addOptionalParameter("carrno", carrno)
    }.firstOrNull()
  }

  fun salvaMotivoDevolucao(notaRecebimento: NotaRecebimentoDev, tipoDevolucaoNovo: Int) {
    val sql = "/sqlSaci/motivoDevolucaoSave.sql"
    script(sql) {
      addOptionalParameter("numero", notaRecebimento.numeroDevolucao)
      addOptionalParameter("situacaoDev", notaRecebimento.situacaoDev)
      addOptionalParameter("tipoDevolucao", notaRecebimento.tipoDevolucao)
      addOptionalParameter("tipoDevolucaoNovo", tipoDevolucaoNovo)
    }
  }

  fun proximoNumeroDevolucao(): Int {
    val sql = "/sqlSaci/proximoNumeroDevolucao.sql"
    return query(sql, Count::class).firstOrNull()?.quant ?: 1
  }

  fun desfazerDevolucao(notaRecebimentoProduto: NotaRecebimentoProduto) {
    val sql = "/sqlSaci/desfazerDevolucao.sql"
    script(sql) {
      addOptionalParameter("invno", notaRecebimentoProduto.ni)
      addOptionalParameter("prdno", notaRecebimentoProduto.prdno)
      addOptionalParameter("grade", notaRecebimentoProduto.grade)
    }
  }

  fun findDevolucoes(notaRecebimentoProduto: NotaRecebimentoProduto): List<DevolucaoProduto> {
    val sql = "/sqlSaci/devolucoes.sql"
    return query(sql, DevolucaoProduto::class) {
      addOptionalParameter("invno", notaRecebimentoProduto.ni)
      addOptionalParameter("prdno", notaRecebimentoProduto.prdno)
      addOptionalParameter("grade", notaRecebimentoProduto.grade)
    }
  }

  fun findDadosNotaDevolucao(numero: Int): List<DadosDevolucao> {
    val sql = "/sqlSaci/findDadosDevolucao.sql"
    return query(sql, DadosDevolucao::class) {
      addOptionalParameter("numero", numero)
    }
  }

  fun removerNotaRecebimentoDev(dev: NotaRecebimentoDev) {
    val sql = "/sqlSaci/removerNotaRecebimentoDev.sql"
    script(sql) {
      addOptionalParameter("situacaoDev", dev.situacaoDev)
      addOptionalParameter("tipoDevolucao", dev.tipoDevolucao)
      addOptionalParameter("numero", dev.numeroDevolucao)
    }
  }

  fun saveNotaRecebimentoProduto(produto: NotaRecebimentoProdutoDev, gradeNova: String) {
    val sql = "/sqlSaci/saveNotaRecebimentoProdutoDev.sql"

    if (produto.ni == null)
      return
    script(sql) {
      addOptionalParameter("invno", produto.ni)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
      addOptionalParameter("gradeNova", gradeNova)
      addOptionalParameter("numero", produto.numeroDevolucao)
      addOptionalParameter("situacaoDev", produto.situacaoDev)
      addOptionalParameter("tipoDevolucao", produto.tipoDevolucao)
      addOptionalParameter("quantDevolucao", produto.quantDevolucao ?: 0)
    }
  }

  fun deleteNotaRecebimentoProduto(produto: NotaRecebimentoProdutoDev) {
    val sql = "/sqlSaci/deleteNotaRecebimentoProdutoDev.sql"

    if (produto.ni == null)
      return
    script(sql) {
      addOptionalParameter("invno", produto.ni)
      addOptionalParameter("prdno", produto.prdno)
      addOptionalParameter("grade", produto.grade)
      addOptionalParameter("numero", produto.numeroDevolucao)
      addOptionalParameter("situacaoDev", produto.situacaoDev)
      addOptionalParameter("tipoDevolucao", produto.tipoDevolucao)
    }
  }

  fun findDadosNota(storeno: Int?, pdvno: Int?, xano: Int?): List<DadosNotaSaida> {
    storeno ?: return emptyList()
    pdvno ?: return emptyList()
    xano ?: return emptyList()

    val sql = "/sqlSaci/findNotaRecebimentoProdutoDevRelatorio.sql"

    return query(sql, DadosNotaSaida::class) {
      addOptionalParameter("storeno", storeno)
      addOptionalParameter("pdvno", pdvno)
      addOptionalParameter("xano", xano)
    }
  }

  fun findDadosRessuprimento(filtro: FiltroDadosProdutosRessuprimento): List<DadosProdutosRessuprimento> {
    val sql = "/sqlSaci/findDadosRessuprimento.sql"
    return query(sql, DadosProdutosRessuprimento::class) {
      addOptionalParameter("loja", filtro.loja)
      addOptionalParameter("pesquisa", filtro.pesquisa)
      addOptionalParameter("dataInicial", filtro.dataInicial.toSaciDate())
      addOptionalParameter("dataFinal", filtro.dataFinal.toSaciDate())
    }
  }

  fun removeDadosRessuprimento(ressuprimento: DadosProdutosRessuprimento) {
    val sql = "/sqlSaci/removeDadosRessuprimento.sql"
    script(sql) {
      addOptionalParameter("loja", ressuprimento.loja)
      addOptionalParameter("pedido", ressuprimento.pedido)
      addOptionalParameter("prdno", ressuprimento.prdno)
      addOptionalParameter("grade", ressuprimento.grade)
      addOptionalParameter("seqno", ressuprimento.seqno)
    }
  }

  fun saveDadosRessuprimento(ressuprimento: DadosProdutosRessuprimento) {
    val sql = "/sqlSaci/saveDadosRessuprimento.sql"
    script(sql) {
      addOptionalParameter("loja", ressuprimento.loja)
      addOptionalParameter("pedido", ressuprimento.pedido)
      addOptionalParameter("prdno", ressuprimento.prdno)
      addOptionalParameter("grade", ressuprimento.grade)
      addOptionalParameter("seqno", ressuprimento.seqno)
      addOptionalParameter("qttyPedida", ressuprimento.qttyPedida)
    }
  }

  fun notaSaidaObservacaoSave(nota: NotaSaidaDev) {
    val sql = "/sqlSaci/notaSaidaObservacaoSave.sql"
    script(sql) {
      addOptionalParameter("storeno", nota.loja)
      addOptionalParameter("pdvno", nota.pdvno)
      addOptionalParameter("xano", nota.xano)
      addOptionalParameter("observacao", nota.observacaoAdd)
    }
  }

  fun findNotaSaidaDevolucaoProduto(nota: NotaSaidaDev): List<NotaSaidaDevProduto> {
    val sql = "/sqlSaci/findNotaSaidaDevolucaoProduto.sql"
    return query(sql, NotaSaidaDevProduto::class) {
      addOptionalParameter("storeno", nota.loja)
      addOptionalParameter("pdvno", nota.pdvno)
      addOptionalParameter("xano", nota.xano)
    }
  }

  companion object {
    private val db = DB("saci")

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
  var quant: Int? = 0
}