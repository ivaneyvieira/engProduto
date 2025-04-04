package br.com.astrosoft.produto.viewmodel.devFor2

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevFor2ViewModel(view: IDevFor2View) : ViewModel<IDevFor2View>(view) {
  val tabNotaPendenciaViewModel = TabNotaPendenciaViewModel(this)
  val tabNotaNFDViewModel = TabNotaNFDViewModel(this)
  val tabNotaTransportadoraViewModel = TabNotaTransportadoraViewModel(this)
  val tabNotaEmailViewModel = TabNotaEmailViewModel(this)
  val tabNotaUsrViewModel = TabNotaUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabNotaPendencia,
    view.tabNotaNFD,
    view.tabNotaTransportadora,
    view.tabNotaEmail,
    view.tabNotaUsr,
  )
}

interface IDevFor2View : IView {
  val tabNotaPendencia: ITabNotaPendencia
  val tabNotaNFD: ITabNotaNFD
  val tabNotaTransportadora: ITabNotaTransportadora
  val tabNotaEmail: ITabNotaEmail
  val tabNotaUsr: ITabNotaUsr
}

