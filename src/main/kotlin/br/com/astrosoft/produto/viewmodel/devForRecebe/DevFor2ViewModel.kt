package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevFor2ViewModel(view: IDevFor2View) : ViewModel<IDevFor2View>(view) {
  val tabNotaEntradaViewModel = TabNotaEntradaViewModel(this)
  val tabNotaEditorViewModel = TabNotaEditorViewModel(this)
  val tabNotaPendenciaViewModel = TabNotaPendenciaViewModel(this)
  val tabNotaColetaViewModel = TabNotaColetaViewModel(this)
  val tabNotaNFDViewModel = TabNotaNFDViewModel(this)
  val tabNotaNFDAbertaViewModel = TabNotaNFDAbertaViewModel(this)
  val tabNotaTransportadoraViewModel = TabNotaTransportadoraViewModel(this)
  val tabNotaEmailViewModel = TabNotaEmailViewModel(this)
  val tabPedidoGarantiaViewModel = TabPedidoGarantiaViewModel(this)
  val tabNotaGarantiaViewModel = TabNotaGarantiaViewModel(this)
  val tabNotaRepostoViewModel = TabNotaRepostoViewModel(this)
  val tabNotaAcertoViewModel = TabNotaAcertoViewModel(this)
  val tabNotaAcertoPagoViewModel = TabNotaAcertoPagoViewModel(this)
  val tabNotaAjusteViewModel = TabNotaAjusteViewModel(this)
  val tabNotaDescarteViewModel = TabNotaDescarteViewModel(this)
  val tabNotaUsrViewModel = TabNotaUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabNotaEntrada,
    view.tabNotaEditor,
    view.tabNotaPendencia,
    view.tabNotaNFD,
    view.tabNotaColeta,
    view.tabNotaNFDAberta,
// view.tabPedidoGarantia,
    // view.tabNotaGarantia,
    view.tabNotaTransportadora,
    view.tabNotaEmail,
    view.tabNotaReposto,
    view.tabNotaAcerto,
    view.tabNotaAcertoPago,
    view.tabNotaAjuste,
    view.tabNotaDescarte,
    view.tabNotaUsr,
  )
}

interface IDevFor2View : IView {
  val tabNotaEntrada: ITabNotaEntrada
  val tabNotaEditor: ITabNotaEditor
  val tabNotaPendencia: ITabNotaPendencia
  val tabNotaColeta: ITabNotaColeta
  val tabNotaNFD: ITabNotaNFD
  val tabNotaNFDAberta: ITabNotaNFDAberta
  val tabPedidoGarantia: ITabPedidoGarantia
  val tabNotaGarantia: ITabNotaGarantia
  val tabNotaTransportadora: ITabNotaTransportadora
  val tabNotaEmail: ITabNotaEmail
  val tabNotaReposto: ITabNotaReposto
  val tabNotaAcerto: ITabNotaAcerto
  val tabNotaAcertoPago: ITabNotaAcertoPago
  val tabNotaAjuste: ITabNotaAjuste
  val tabNotaDescarte: ITabNotaDescarte
  val tabNotaUsr: ITabNotaUsr
}

