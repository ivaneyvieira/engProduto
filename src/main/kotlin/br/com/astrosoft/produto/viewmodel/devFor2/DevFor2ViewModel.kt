package br.com.astrosoft.produto.viewmodel.devFor2

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevFor2ViewModel(view: IDevFor2View) : ViewModel<IDevFor2View>(view) {
  val tabNotaPendenciaViewModel = TabNotaPendenciaViewModel(this)
  val tabNotaNFDViewModel = TabNotaNFDViewModel(this)
  val tabNotaTransportadoraViewModel = TabNotaTransportadoraViewModel(this)
  val tabNotaEmailViewModel = TabNotaEmailViewModel(this)
  val tabPedidoGarantiaViewModel = TabPedidoGarantiaViewModel(this)
  val tabNotaGarantiaViewModel = TabNotaGarantiaViewModel(this)
  val tabNotaRepostoViewModel = TabNotaRepostoViewModel(this)
  val tabNotaAcertoViewModel = TabNotaAcertoViewModel(this)
  val tabNotaUsrViewModel = TabNotaUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabNotaPendencia,
    view.tabNotaNFD,
    view.tabPedidoGarantia,
    view.tabNotaGarantia,
    view.tabNotaTransportadora,
    view.tabNotaEmail,
    view.tabNotaReposto,
    view.tabNotaAcerto,
    view.tabNotaUsr,
  )
}

interface IDevFor2View : IView {
  val tabNotaPendencia: ITabNotaPendencia
  val tabNotaNFD: ITabNotaNFD
  val tabPedidoGarantia: ITabPedidoGarantia
  val tabNotaGarantia: ITabNotaGarantia
  val tabNotaTransportadora: ITabNotaTransportadora
  val tabNotaEmail: ITabNotaEmail
  val tabNotaReposto: ITabNotaReposto
  val tabNotaAcerto: ITabNotaAcerto
  val tabNotaUsr: ITabNotaUsr
}

