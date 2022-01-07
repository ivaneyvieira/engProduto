package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class NotaViewModel(view: INotaView) : ViewModel<INotaView>(view) {
  val tabNotaBaseViewModel = TabNotaBaseViewModel(this)
  val tabNotaEntregaViewModel = TabNotaEntregaViewModel(this)

  override fun listTab() = listOf(view.tabNotaBase, view.tabNotaEntrega)
}

interface INotaView : IView {
  val tabNotaBase: ITabNotaBase
  val tabNotaEntrega: ITabNotaEntrega
}

