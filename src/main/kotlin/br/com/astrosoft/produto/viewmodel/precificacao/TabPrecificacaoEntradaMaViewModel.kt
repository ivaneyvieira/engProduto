package br.com.astrosoft.produto.viewmodel.precificacao

class TabPrecificacaoEntradaMaViewModel(viewModel: PrecificacaoViewModel) :
  TabPrecificacaoAbstractViewModel(viewModel) {
  override val subView
    get() = viewModel.view.tabPrecificacaoEntradaMaViewModel
}

