package br.com.astrosoft.produto.viewmodel.devFor2

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevFor2ViewModel(view: IRecebimentoView) : ViewModel<IRecebimentoView>(view) {
  val tabNotaDevForViewModel = TabNotaDevForViewModel(this)

  override fun listTab() = listOf(
    view.tabNotaDevFor,
  )
}

interface IRecebimentoView : IView {
  val tabNotaDevFor: ITabNotaDevFor
}

