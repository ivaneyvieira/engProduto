package br.com.astrosoft.produto.viewmodel.precificacao

class TabPrecificacaoSaidaViewModel(viewModel: PrecificacaoViewModel) : TabPrecificacaoAbstractViewModel(viewModel) {
  override val subView
    get() = viewModel.view.tabPrecificacaoSaidaViewModel
}

