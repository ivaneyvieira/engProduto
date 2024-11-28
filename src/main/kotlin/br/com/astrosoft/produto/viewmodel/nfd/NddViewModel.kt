package br.com.astrosoft.produto.viewmodel.nfd

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class NfdViewModel(view: INfdView) : ViewModel<INfdView>(view) {
  val tabNfdDevForViewModel = TabNfdDevForViewModel(this)
  val tabNfdUsrViewModel = TabNfdUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabNfdDevFor,
    view.tabNfdUsr,
  )
}

interface INfdView : IView {
  val tabNfdDevFor: ITabNfdDevFor
  val tabNfdUsr: ITabNfdUsr
}

