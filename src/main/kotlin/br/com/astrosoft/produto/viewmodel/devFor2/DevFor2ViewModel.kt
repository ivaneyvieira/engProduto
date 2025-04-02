package br.com.astrosoft.produto.viewmodel.devFor2

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevFor2ViewModel(view: IDevFor2View) : ViewModel<IDevFor2View>(view) {
  val tabNotaPendenciaViewModel = TabNotaPendenciaViewModel(this)

  override fun listTab() = listOf(
    view.tabNotaPendencia,
  )
}

interface IDevFor2View : IView {
  val tabNotaPendencia: ITabNotaPendencia
}

