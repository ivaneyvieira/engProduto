package br.com.astrosoft.produto.viewmodel.precificacao

import br.com.astrosoft.produto.viewmodel.precificacao.PrecificacaoViewModel
import br.com.astrosoft.produto.viewmodel.precificacao.TabPrecificacaoAbstractViewModel

class TabPrecificacaoSaidaViewModel(viewModel: PrecificacaoViewModel) : TabPrecificacaoAbstractViewModel(viewModel) {
  override val subView
    get() = viewModel.view.tabPrecificacaoSaidaViewModel
}

