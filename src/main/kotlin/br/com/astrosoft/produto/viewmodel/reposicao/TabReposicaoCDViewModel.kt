package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroReposicao
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.Reposicao

class TabReposicaoCDViewModel(val viewModel: ReposicaoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val reposicoes = Reposicao.findAll(filtro)
    subView.updateReposicoes(reposicoes)
  }

  val subView
    get() = viewModel.view.tabReposicaoCD
}

interface ITabReposicaoCD : ITabView {
  fun filtro(): FiltroReposicao
  fun updateReposicoes(reposicoes: List<Reposicao>)
}