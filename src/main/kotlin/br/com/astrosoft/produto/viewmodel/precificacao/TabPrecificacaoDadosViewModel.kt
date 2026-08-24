package br.com.astrosoft.produto.viewmodel.precificacao

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.DadosPrecificacao
import br.com.astrosoft.produto.model.beans.FiltroDadosPrecificacao

class TabPrecificacaoDadosViewModel(val viewModel: PrecificacaoViewModel) {
  val subView
    get() = viewModel.view.tabPrecificacaoDadosViewModel

  fun updateView() {
    val filtro = subView.filtro()
    val filtroAtual = TabPrecificacaoDadosViewModel.filtro
    val list = if (filtroAtual == filtro) {
      list ?: emptyList()
    } else {
      list = DadosPrecificacao.findAll(filtro)
      list ?: emptyList()
    }
    subView.updateGrid(list)
  }

  companion object {
    private var filtro: FiltroDadosPrecificacao? = null
    private var list: List<DadosPrecificacao>? = null
  }
}

interface ITabPrecificacaoDadosViewModel : ITabView {
  fun filtro(): FiltroDadosPrecificacao
  fun updateGrid(itens: List<DadosPrecificacao>)
}
