package br.com.astrosoft.produto.viewmodel.notaSaida

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class NotaViewModel(view: INotaView) : ViewModel<INotaView>(view) {
  val tabNotaExpViewModel = TabNotaExpViewModel(this)
  val tabNotaCDViewModel = TabNotaCDViewModel(this)
  val tabNotaEntViewModel = TabNotaEntViewModel(this)

  override fun listTab() = listOf(view.tabNotaExp, view.tabNotaCD, view.tabNotaEnt)
}

interface INotaView : IView {
  val tabNotaExp: ITabNotaExp
  val tabNotaCD: ITabNotaCD
  val tabNotaEnt: ITabNotaEnt
}

