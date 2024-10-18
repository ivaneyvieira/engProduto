package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintRessuprimento
import br.com.astrosoft.produto.model.report.ReportRessuprimentoEntradaSobra
import br.com.astrosoft.produto.model.saci

class TabRessuprimentoEntViewModel(val viewModel: RessuprimentoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro(EMarcaRessuprimento.ENT)
    val ressuprimento = if (subView.filtroProduto())
      Ressuprimento.find(filtro.prdno, filtro.grade)
    else
      Ressuprimento.find(filtro)
    subView.updateRessuprimentos(ressuprimento/*.filter {
      (it.countCor ?: 0) == 0
    }*/)
  }

  fun formAutoriza(pedido: Ressuprimento) {
    subView.formAutoriza(pedido)
  }

  fun recebidoPedido(pedido: Ressuprimento, login: String, senha: String) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    pedido.autorizaRecebido(user)

    updateView()
  }

  fun autorizaPedido(pedido: Ressuprimento, login: String, senha: String) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    pedido.autoriza(user)

    updateView()
  }

  fun transportadoPedido(pedido: Ressuprimento, numero: Int) = viewModel.exec {
    val funcionario = saci.listFuncionario(numero) ?: fail("Funcionário não encontrado")
    pedido.transportado(funcionario)
    updateView()
  }

  fun previewPedido(pedido: Ressuprimento, printEvent: (impressora: String) -> Unit) = viewModel.exec {
    if (pedido.entreguePor.isNullOrBlank())
      fail("Pedido não autorizado")

    if (pedido.transportadoPor.isNullOrBlank())
      fail("Pedido não transportado")

    val user = AppConfig.userLogin() as? UserSaci

    if (pedido.recebidoPor.isNullOrBlank() && user?.ressuprimentoRecebedor == true && !user.admin)
      fail("Pedido não recebido")

    val produtos = pedido.produtos()

    val relatorio = PrintRessuprimento(pedido, ProdutoRessuprimento::qtPedido)

    relatorio.print(
      dados = produtos.sortedWith(
        compareBy(
          ProdutoRessuprimento::descricao,
          ProdutoRessuprimento::codigo,
          ProdutoRessuprimento::grade
        )
      ),
      printer = subView.printerPreview(loja = 1, printEvent = printEvent)
    )
  }

  fun formTransportado(pedido: Ressuprimento) {
    subView.formTransportado(pedido)
  }

  fun formRecebido(pedido: Ressuprimento) {
    subView.formRecebido(pedido)
  }

  fun marcaImpressao(pedido: Ressuprimento) = viewModel.exec {
    val usuario = AppConfig.userLogin() as? UserSaci
    if (usuario?.ressuprimentoRecebedor == true) {
      if (pedido.recebidoPor.isNullOrBlank()) fail("Pedido não recebido")
      pedido.produtos().forEach {
        it.marca = EMarcaRessuprimento.REC.num
        it.salva()
      }
      updateView()
    }
  }

  fun selecionaProdutos(codigoBarra: String) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.selecionado = EMarcaRessuprimento.REC.num
    produto.salva()

    subView.updateProduto(produto)
  }

  fun marca() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter {
      val qtRecebido = it.qtRecebido ?: 0
      val qtQuantNF = it.qtQuantNF ?: 0
      it.selecionado == EMarcaRessuprimento.REC.num &&
      (qtRecebido == qtQuantNF) &&
      (it.codigoCorrecao.isNullOrBlank()) &&
      (it.gradeCorrecao.isNullOrBlank())
    }
    itens.ifEmpty {
      fail("Recebimento diferente da Entrega")
    }

    itens.forEach { produto ->
      produto.marca = EMarcaRessuprimento.REC.num
      produto.selecionado = EMarcaRessuprimento.REC.num
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun desmarcar() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter { it.selecionado == EMarcaRessuprimento.REC.num }
    itens.ifEmpty {
      fail("Nenhum produto para desmarcar")
    }

    itens.forEach { produto ->
      produto.selecionado = 0
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun saveQuant(bean: ProdutoRessuprimento) {
    bean.salva()
    subView.updateProdutos()
  }

  fun findGrades(codigo: String): List<PrdGrade> {
    return saci.findGrades(codigo)
  }

  fun processamentoProdutos() {
    val selecionados = subView.ressuprimentosSelecionados()
    if (selecionados.isEmpty()) fail("Nenhum ressuprimento selecionado")
    subView.showDlgProdutos(selecionados)
  }

  fun imprimeRelatorio(ressuprimentoTitle: String) {
    val produtos = subView.produtosSelecionados()
    val produtosSobra = produtoRessuprimentoSobras(produtos)

    if (produtosSobra.isEmpty()) fail("Nenhum produto para imprimir")

    val report = ReportRessuprimentoEntradaSobra(ressuprimentoTitle)
    val file = report.processaRelatorio(produtosSobra)
    viewModel.view.showReport(chave = "Ressuprimento${System.nanoTime()}", report = file)
  }

  private fun produtoRessuprimentoSobras(produtos: List<ProdutoRessuprimento>): List<ProdutoRessuprimentoSobra> {
    val listFalta = sequence {
      produtos.forEach {
        val qtQuantNF = it.qtQuantNF ?: 0
        val qtRecebido = (it.qtRecebido ?: 0) + (it.qtAvaria ?: 0) + (it.qtVencido ?: 0)

        if (qtQuantNF > qtRecebido) {
          yield(
            ProdutoRessuprimentoSobra(
              orderGroup = 1,
              grupo = "Falta",
              codigo = it.codigo ?: "",
              descricao = it.descricao ?: "",
              grade = it.grade ?: "",
              nota = it.numeroNota ?: "",
              localizacao = it.localizacao ?: "",
              quantidade = (it.qtQuantNF ?: 0) - (it.qtRecebido ?: 0),
            )
          )
        }
      }
    }.toList().sortedWith(compareBy(ProdutoRessuprimentoSobra::descricao))

    val listaSobra = sequence {
      produtos.forEach {
        if (it.codigoCorrecao?.isNotEmpty() == true) {
          val qtEntregue = if (it.qtEntregue == 0) null else it.qtEntregue
          yield(
            ProdutoRessuprimentoSobra(
              orderGroup = 2,
              grupo = "Sobra",
              codigo = it.codigoCorrecao ?: "",
              descricao = it.descricaoCorrecao ?: "",
              grade = it.gradeCorrecao ?: "",
              nota = "",
              localizacao = it.localizacao ?: "",
              quantidade = qtEntregue,
            )
          )
        }

        val qtQuantNF = it.qtQuantNF ?: 0
        val qtRecebido = (it.qtRecebido ?: 0) + (it.qtAvaria ?: 0) + (it.qtVencido ?: 0)

        if (qtQuantNF < qtRecebido) {
          yield(
            ProdutoRessuprimentoSobra(
              orderGroup = 2,
              grupo = "Sobra",
              codigo = it.codigo ?: "",
              descricao = it.descricao ?: "",
              grade = it.grade ?: "",
              nota = it.numeroNota ?: "",
              localizacao = it.localizacao ?: "",
              quantidade = qtRecebido - qtQuantNF,
            )
          )
        }
      }
    }.toList().sortedWith(compareBy(ProdutoRessuprimentoSobra::descricao))

    val listAvaria = sequence {
      produtos.forEach {
        if ((it.qtAvaria ?: 0) > 0) {
          yield(
            ProdutoRessuprimentoSobra(
              orderGroup = 3,
              grupo = "Avaria",
              codigo = it.codigo ?: "",
              descricao = it.descricao ?: "",
              grade = it.grade ?: "",
              nota = it.numeroNota ?: "",
              localizacao = it.localizacao ?: "",
              quantidade = it.qtAvaria ?: 0,
            )
          )
        }
      }
    }.toList()

    val listVencido = sequence {
      produtos.forEach {
        if ((it.qtVencido ?: 0) > 0) {
          yield(
            ProdutoRessuprimentoSobra(
              orderGroup = 4,
              grupo = "Vencido",
              codigo = it.codigo ?: "",
              descricao = it.descricao ?: "",
              grade = it.grade ?: "",
              nota = it.numeroNota ?: "",
              localizacao = it.localizacao ?: "",
              quantidade = it.qtVencido ?: 0,
            )
          )
        }
      }
    }.toList()

    val produtosSobra = listFalta + listaSobra + listAvaria + listVencido
    return produtosSobra.sortedWith(
      compareBy(
        ProdutoRessuprimentoSobra::orderGroup,
        ProdutoRessuprimentoSobra::descricao
      )
    )
  }

  val subView
    get() = viewModel.view.tabRessuprimentoEnt
}

interface ITabRessuprimentoEnt : ITabView {
  fun filtro(marca: EMarcaRessuprimento): FiltroRessuprimento
  fun updateRessuprimentos(ressuprimentos: List<Ressuprimento>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoRessuprimento>
  fun formAutoriza(pedido: Ressuprimento)
  fun formTransportado(pedido: Ressuprimento)
  fun formRecebido(pedido: Ressuprimento)
  fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento?
  fun updateProduto(produto: ProdutoRessuprimento)
  fun produtosSelecionados(): List<ProdutoRessuprimento>
  fun ressuprimentosSelecionados(): List<Ressuprimento>
  fun showDlgProdutos(ressuprimentos: List<Ressuprimento>)
  fun filtroProduto(): Boolean
}