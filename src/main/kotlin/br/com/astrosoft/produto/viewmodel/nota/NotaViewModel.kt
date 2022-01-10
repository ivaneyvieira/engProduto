package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class NotaViewModel(view: INotaView) : ViewModel<INotaView>(view) {
  val tabNotaExpViewModel = TabNotaExpViewModel(this)
  val tabNotaCDViewModel = TabNotaCDViewModel(this)

  override fun listTab() = listOf(view.tabNotaExp, view.tabNotaCD)
}

interface INotaView : IView {
  val tabNotaExp: ITabNotaExp
  val tabNotaCD: ITabNotaCD
}

