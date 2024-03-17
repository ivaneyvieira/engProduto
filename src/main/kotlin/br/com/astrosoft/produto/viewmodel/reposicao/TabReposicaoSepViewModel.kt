package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroReposicao
import br.com.astrosoft.produto.model.beans.Reposicao

class TabReposicaoSepViewModel(val viewModel: ReposicaoViewModel) {
  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val reposicoes = Reposicao.findAll(filtro)
    subView.updateReposicoes(reposicoes)
  }

  val subView
    get() = viewModel.view.tabReposicaoSep
}

interface ITabReposicaoSep : ITabView {
  fun filtro(): FiltroReposicao
  fun updateReposicoes(reposicoes: List<Reposicao>)
}