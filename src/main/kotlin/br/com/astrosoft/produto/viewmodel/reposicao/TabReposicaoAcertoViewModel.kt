package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

class TabReposicaoAcertoViewModel(val viewModel: ReposicaoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val reposicoes = reposicoes()
    subView.updateUsuarios(reposicoes)
  }

  private fun reposicoes(): List<Reposicao> {
    val filtro = subView.filtro()
    val reposicoes = Reposicao.findAll(filtro).filter {
      it.countSep() > 0
    }
    return reposicoes
  }

  fun selecionaProdutos(codigoBarra: String?) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.selecionado = EMarcaReposicao.ENT.num
    produto.salva()
    subView.updateProduto(produto)
  }

  fun marca() = viewModel.exec {
    val itens = subView.produtosSelecionados()
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
    val itens = subView.produtosSelecionados()
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
    subView.updateUsuarios(reposicoes)
    subView.updateProdutos(reposicoes)
  }

  fun salva(bean: Reposicao) {
    bean.salva()
    updateView()
  }

  fun formEntregue(pedido: Reposicao) {
    subView.formEntregue(pedido)
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

  val subView
    get() = viewModel.view.tabReposicaoAcerto
}

interface ITabReposicaoAcerto : ITabView {
  fun filtro(): FiltroReposicao
  fun updateUsuarios(reposicoes: List<Reposicao>)
  fun produtosCodigoBarras(codigoBarra: String?): ReposicaoProduto?
  fun produtosSelecionados(): List<ReposicaoProduto>
  fun updateProduto(produto: ReposicaoProduto)
  fun updateProdutos(reposicoes: List<Reposicao>)
  fun formEntregue(pedido: Reposicao)
}