package br.com.astrosoft.produto.viewmodel.precificacao

import br.com.astrosoft.produto.viewmodel.precificacao.PrecificacaoViewModel
import br.com.astrosoft.produto.viewmodel.precificacao.TabPrecificacaoAbstractViewModel

open class TabPrecificacaoViewModel(viewModel: PrecificacaoViewModel) : TabPrecificacaoAbstractViewModel(viewModel) {
  override val subView
    get() = viewModel.view.tabPrecificacaoViewModel
}

