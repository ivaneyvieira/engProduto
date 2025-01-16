package br.com.astrosoft.produto.viewmodel.precificacao

open class TabPrecificacaoViewModel(viewModel: PrecificacaoViewModel) : TabPrecificacaoAbstractViewModel(viewModel) {
  override val subView
    get() = viewModel.view.tabPrecificacaoViewModel
}

