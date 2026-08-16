package br.com.astrosoft.produto.viewmodel.precificacao

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.DadosPrecificacao
import br.com.astrosoft.produto.model.beans.FiltroDadosPrecificacao

class TabPrecificacaoDadosViewModel(val viewModel: PrecificacaoViewModel) {
  val subView
    get() = viewModel.view.tabPrecificacaoDadosViewModel


  fun updateView() {
    val filtro = subView.filtro()
    val list = DadosPrecificacao.findAll(filtro)
    subView.updateGrid(list)
  }
}

interface ITabPrecificacaoDadosViewModel : ITabView {
  fun filtro(): FiltroDadosPrecificacao
  fun updateGrid(itens: List<DadosPrecificacao>)
}
