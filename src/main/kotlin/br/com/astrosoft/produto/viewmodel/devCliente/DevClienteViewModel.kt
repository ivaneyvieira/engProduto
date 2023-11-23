package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevClienteViewModel(view: IDevClienteView) : ViewModel<IDevClienteView>(view) {
  val tabDevCliValeTrocaViewModel = TabDevCliValeTrocaViewModel(this)
  val tabDevCliValeTrocaImpViewModel = TabDevCliValeTrocaImpViewModel(this)

  override fun listTab() = listOf(
    view.tabDevCliValeTroca, view.tabDevCliValeTrocaImp,
  )
}

interface IDevClienteView : IView {
  val tabDevCliValeTroca: ITabDevCliValeTroca
  val tabDevCliValeTrocaImp: ITabDevCliValeTrocaImp
}