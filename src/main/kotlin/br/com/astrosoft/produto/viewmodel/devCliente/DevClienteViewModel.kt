package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevClienteViewModel(view: IDevClienteView) : ViewModel<IDevClienteView>(view) {
  val tabDevCliValeTrocaViewModel = TabDevCliValeTrocaViewModel(this)

  override fun listTab() = listOf(
    view.tabDevCliValeTroca,
  )
}

interface IDevClienteView : IView {
  val tabDevCliValeTroca: ITabDevCliValeTroca
}