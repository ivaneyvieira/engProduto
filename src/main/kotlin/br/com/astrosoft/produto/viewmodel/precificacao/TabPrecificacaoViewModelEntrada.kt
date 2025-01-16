package br.com.astrosoft.produto.viewmodel.precificacao

class TabPrecificacaoEntradaViewModel(viewModel: PrecificacaoViewModel) : TabPrecificacaoAbstractViewModel(viewModel) {
  override val subView
    get() = viewModel.view.tabPrecificacaoEntradaViewModel
}

