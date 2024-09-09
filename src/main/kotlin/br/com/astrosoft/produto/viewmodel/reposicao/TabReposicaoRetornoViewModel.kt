package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

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

  fun marca() = viewModel.exec {
    val itens = subView.produtosSelecionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }

    subView.assinaProdutos { user ->
      itens.forEach { produto ->
        produto.marca = EMarcaReposicao.ENT.num
        produto.selecionado = EMarcaReposicao.ENT.num
        produto.qtRecebido = produto.quantidade
        produto.entregueNo = user.no
        produto.salva()
      }

      updateProdutos()
    }
  }

  fun desmarcar() = viewModel.exec {
    val itens = subView.produtosSelecionados()
    itens.ifEmpty {
      fail("Nenhum produto para desmarcar")
    }

    itens.forEach { produto ->
      produto.selecionado = EMarcaReposicao.SEP.num
      produto.qtRecebido = 0
      produto.entregueNo = 0
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
    val reposicaoDlg = subView.reposicaoDlg()
    subView.updateReposicoes(reposicoes)
    val reposicaoNova = reposicoes.firstOrNull { it.chave() == reposicaoDlg?.chave() }
    subView.updateProdutos(reposicaoNova)
  }

  fun salva(bean: Reposicao) {
    bean.salva()
    updateView()
  }

  fun entregueProdutos(login: String, senha: String, marca: (UserSaci) -> Unit) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    marca(user)
  }

  val subView
    get() = viewModel.view.tabReposicaoRetorno
}

interface ITabReposicaoRetorno : ITabView {
  fun filtro(): FiltroReposicao
  fun updateReposicoes(reposicoes: List<Reposicao>)
  fun produtosCodigoBarras(codigoBarra: String?): ReposicaoProduto?
  fun produtosSelecionados(): List<ReposicaoProduto>
  fun updateProduto(produto: ReposicaoProduto)
  fun updateProdutos(reposicao: Reposicao?)
  fun assinaProdutos(marca: (UserSaci) -> Unit)
  fun reposicaoDlg(): Reposicao?
}