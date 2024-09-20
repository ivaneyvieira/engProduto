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
    subView.updateReposicoes(reposicoes)
  }

  private fun reposicoes(): List<Reposicao> {
    val filtro = subView.filtro()
    val reposicoes = Reposicao.findAll(filtro)
    return reposicoes
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

  fun recebeFinalizacao(reposicao: Reposicao, login: String, senha: String) {
    val lista = UserSaci.findAll()
    val user = lista.firstOrNull {
      it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
    }
    user ?: fail("Usuário ou senha inválidos")

    val produtosSelecionado = subView.produtosSelecionado().ifEmpty {
      fail("Nenhum produto selecionado")
    }

    reposicao.finaliza(user, produtosSelecionado)

    updateView()
  }

  fun entregaReposicao(reposicao: Reposicao, login: String, senha: String) {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    val produtosSelecionado = subView.produtosSelecionado().ifEmpty {
      fail("Nenhum produto selecionado")
    }

    reposicao.entregue(user, produtosSelecionado)

    updateView()
  }

  val subView
    get() = viewModel.view.tabReposicaoAcerto
}

interface ITabReposicaoAcerto : ITabView {
  fun filtro(): FiltroReposicao
  fun updateReposicoes(reposicoes: List<Reposicao>)
  fun produtosCodigoBarras(codigoBarra: String?): ReposicaoProduto?
  fun produtosList(): List<ReposicaoProduto>
  fun produtosSelecionado(): List<ReposicaoProduto>
  fun updateProduto(produto: ReposicaoProduto)
}