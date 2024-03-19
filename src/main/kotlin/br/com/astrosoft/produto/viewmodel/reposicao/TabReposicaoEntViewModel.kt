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
    val reposicoes = Reposicao.findAll(filtro).filter {
      it.countENT() > 0
    }
    return reposicoes
  }

  fun formEntregue(pedido: Reposicao) {
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

  fun recebePedido(pedido: Reposicao, empNo: Int) = viewModel.exec {
    val funcionario = saci.listFuncionario(empNo) ?: fail("Funcionário não encontrado")
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
    if (pedido.entregueNome.isBlank())
      fail("Pedido não autorizado")

    if (pedido.recebidoNome.isBlank())
      fail("Pedido não recebido")

    if (pedido.countSEP() > 0) {
      fail("Pedido com produtos ainda em separação")
    }

    if (pedido.countDivergente() > 0) {
      fail("Pedido com quantidade divergente")
    }

    val produtos = pedido.produtosENT()

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

  val subView
    get() = viewModel.view.tabReposicaoEnt
}

interface ITabReposicaoEnt : ITabView {
  fun filtro(): FiltroReposicao
  fun updateReposicoes(reposicoes: List<Reposicao>)
  fun formEntregue(pedido: Reposicao)
  fun formRecebe(pedido: Reposicao)
  fun updateProdutos(reposicoes: List<Reposicao>)
}