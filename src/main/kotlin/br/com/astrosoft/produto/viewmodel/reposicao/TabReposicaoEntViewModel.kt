package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintReposicao
import br.com.astrosoft.produto.model.saci

class TabReposicaoEntViewModel(val viewModel: ReposicaoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val reposicoes = reposicoes()
    subView.updateReposicoes(reposicoes)
  }

  private fun reposicoes(): List<Reposicao> {
    val filtro = subView.filtro()
    val reposicoes = if (subView.filtroProduto())
      Reposicao.findAll(filtro.prdno, filtro.grade)
    else
      Reposicao.findAll(filtro)
    return reposicoes.filter {
      it.countSepNaoAssinado() == 0
    }
  }

  fun formEntregue(pedido: Reposicao) = viewModel.exec {
    subView.formEntregue(pedido)
  }

  fun formRecebido(pedido: Reposicao) {
    subView.formRecebe(pedido)
  }

  fun entreguePedido(pedido: Reposicao, login: String, senha: String) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    pedido.entregue(user)

    updateView()
  }

  fun recebePedido(pedido: Reposicao, empNo: Int, senha: String) = viewModel.exec {
    val funcionario = saci.listFuncionario(empNo) ?: fail("Funcionário não encontrado")

    if (funcionario.senha != senha) {
      fail("Senha inválida")
    }

    pedido.recebe(funcionario)

    updateView()
  }

  fun saveQuant(bean: ReposicaoProduto) {
    bean.salva()
    updateProdutos()
  }

  fun updateProdutos() {
    val reposicoes = reposicoes()
    subView.updateReposicoes(reposicoes)
    subView.updateProdutos(reposicoes)
  }

  fun previewPedido(pedido: Reposicao, printEvent: (impressora: String) -> Unit) = viewModel.exec {
    if (pedido.countNaoEntregue() > 0) {
      if (pedido.metodo == EMetodo.ACERTO.num) {
        fail("Pedido não Conferido")
      } else {
        fail("Pedido não Entregue")
      }
    }

    if (pedido.metodo == EMetodo.ACERTO.num) {
      if (pedido.countNaoFinalizado() > 0) {
        fail("Pedido não Finalizado")
      }
    } else {
      if (pedido.countNaoRecebido() > 0) {
        fail("Pedido não recebido")
      }
    }

    if (pedido.countSep() > 0) {
      fail("Pedido com produtos ainda em separação")
    }

    val produtos = pedido.produtosEnt()

    val relatorio = PrintReposicao()

    relatorio.print(
      dados = produtos.sortedWith(
        compareBy(
          ReposicaoProduto::descricao,
          ReposicaoProduto::codigo,
          ReposicaoProduto::grade
        )
      ),
      printer = subView.printerPreview(loja = 1, printEvent = printEvent)
    )
  }

  fun marcaImpressao(pedido: Reposicao) {
    pedido.expiraPedido()
  }

  fun removePedidos() = viewModel.exec {
    val pedidos = subView.pedidosSelecionados().ifEmpty {
      fail("Nenhum pedido selecionado")
    }
    viewModel.view.showQuestion("Confirma a remoção dos predidos?") {
      pedidos.forEach { reposicao ->
        reposicao.produtos.forEach { produto ->
          produto.marca = EMarcaReposicao.SEP.num
          produto.selecionado = EMarcaReposicao.SEP.num
          produto.recebidoNo = 0
          produto.entregueNo = 0
          produto.recebidoNo = 0
          produto.salva()
        }
      }
      updateView()
    }
  }

  val subView
    get() = viewModel.view.tabReposicaoEnt
}

interface ITabReposicaoEnt : ITabView {
  fun filtro(): FiltroReposicao
  fun updateReposicoes(reposicoes: List<Reposicao>)
  fun formEntregue(pedido: Reposicao)
  fun formRecebe(pedido: Reposicao)
  fun updateProdutos(reposicoes: List<Reposicao>)
  fun filtroProduto(): Boolean
  fun pedidosSelecionados(): List<Reposicao>
}