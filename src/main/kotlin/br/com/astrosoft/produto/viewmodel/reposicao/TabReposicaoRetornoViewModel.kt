package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintReposicaoRetorno
import br.com.astrosoft.produto.model.saci

class TabReposicaoRetornoViewModel(val viewModel: ReposicaoViewModel) {
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
    val reposicoes = Reposicao.findAll(filtro)
    return reposicoes
  }

  fun selecionaProdutos(codigoBarra: String?) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.selecionado = EMarcaReposicao.ENT.num
    produto.salva()
    subView.updateProduto(produto)
  }

  fun marca() = viewModel.exec {
    val itens = subView.produtosList().filter { it.isSelecionado() }
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }

    itens.forEach { produto ->
      produto.marca = EMarcaReposicao.ENT.num
      produto.selecionado = EMarcaReposicao.ENT.num
      produto.qtRecebido = produto.quantidade
      produto.salva()
    }
    updateProdutos()
  }

  fun desmarcar() = viewModel.exec {
    val itens = subView.produtosList().filter { it.selecionado == EMarcaReposicao.ENT.num }
    itens.ifEmpty {
      fail("Nenhum produto para desmarcar")
    }

    itens.forEach { produto ->
      produto.selecionado = EMarcaReposicao.SEP.num
      produto.salva()
    }
    updateProdutos()
  }

  fun saveQuant(bean: ReposicaoProduto) {
    bean.salva()
    updateProdutos()
  }

  fun updateProdutos() {
    val reposicoes = reposicoes()
    subView.updateReposicoes(reposicoes)
  }

  fun salva(bean: Reposicao) {
    bean.salva()
    updateView()
  }

  fun recebeReposicao(reposicao: Reposicao, empNo: Int, senha: String) {
    val funcionario = saci.listFuncionario(empNo) ?: fail("Funcionário não encontrado")

    if (funcionario.senha != senha) {
      fail("Senha inválida")
    }

    reposicao.recebe(funcionario)

    updateView()
  }

  fun entregaReposicao(reposicao: Reposicao, login: String, senha: String) {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    reposicao.entregue(user)

    updateView()
  }

  fun previewPedido(pedido: Reposicao, printEvent: (impressora: String) -> Unit) = viewModel.exec {
    if (pedido.entregueNome.isBlank())
      fail("Pedido não autorizado")

    if (pedido.recebidoNome.isBlank())
      fail("Pedido não recebido")

    if (pedido.countSep() > 0) {
      fail("Pedido com produtos ainda em separação")
    }

    val produtos = pedido.produtosEnt()

    val relatorio = PrintReposicaoRetorno()

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
    get() = viewModel.view.tabReposicaoRetorno
}

interface ITabReposicaoRetorno : ITabView {
  fun filtro(): FiltroReposicao
  fun updateReposicoes(reposicoes: List<Reposicao>)
  fun produtosCodigoBarras(codigoBarra: String?): ReposicaoProduto?
  fun produtosList(): List<ReposicaoProduto>
  fun updateProduto(produto: ReposicaoProduto)
}