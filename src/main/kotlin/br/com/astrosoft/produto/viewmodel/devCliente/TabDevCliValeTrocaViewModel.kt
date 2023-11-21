package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView

class TabDevCliValeTrocaViewModel(val viewModel: DevClienteViewModel)  {
  val subView
    get() = viewModel.view.tabDevCliValeTroca
}

interface ITabDevCliValeTroca : ITabView {
}