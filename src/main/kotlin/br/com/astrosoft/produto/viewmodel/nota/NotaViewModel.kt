package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class NotaViewModel(view: INotaView) : ViewModel<INotaView>(view) {
  val tabNotaBaseViewModel = TabNotaBaseViewModel(this)

  override fun listTab() = listOf(view.tabNotaBase)
}

interface INotaView : IView {
  val tabNotaBase: ITabNotaBase
}

