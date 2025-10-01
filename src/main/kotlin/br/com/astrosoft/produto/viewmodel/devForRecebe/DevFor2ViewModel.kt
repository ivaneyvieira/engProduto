package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevFor2ViewModel(view: IDevFor2View) : ViewModel<IDevFor2View>(view) {
  val tabNotaEntradaViewModel = TabNotaEntradaViewModel(this)
  val tabNotaEditorViewModel = TabNotaEditorViewModel(this)
  val tabNotaDivergenteViewModel = TabNotaDivergenteViewModel(this)
  val tabNotaColetaRepViewModel = TabNotaColetaRepViewModel(this)
  val tabNotaPedidoViewModel = TabNotaPedidoViewModel(this)
  val tabNotaColetaViewModel = TabNotaColetaViewModel(this)
  val tabNotaNFDViewModel = TabNotaNFDViewModel(this)
  val tabNotaNFDAbertaViewModel = TabNotaNFDAbertaViewModel(this)
  val tabNotaTransportadoraViewModel = TabNotaTransportadoraViewModel(this)
  val tabNotaEmailViewModel = TabNotaEmailViewModel(this)
  val tabNotaRetornoNFDViewModel = TabNotaRetornoNFDViewModel(this)
  val tabPedidoGarantiaViewModel = TabPedidoGarantiaViewModel(this)
  val tabNotaGarantiaViewModel = TabNotaGarantiaViewModel(this)
  val tabNotaRepostoViewModel = TabNotaRepostoViewModel(this)
  val tabNotaAcertoViewModel = TabNotaAcertoViewModel(this)
  val tabNotaAcertoPagoViewModel = TabNotaAcertoPagoViewModel(this)
  val tabNotaAjusteViewModel = TabNotaAjusteViewModel(this)
  val tabNotaDescarteViewModel = TabNotaDescarteViewModel(this)
  val tabNotaNuloViewModel = TabNotaNuloViewModel(this)
  val tabNotaFornecedorViewModel = TabNotaFornecedorViewModel(this)
  val tabNotaNFDSTNRViewModel = TabNotaNFDSTNRViewModel(this)
  val tabNotaUsrViewModel = TabNotaUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabNotaEntrada,
    view.tabNotaNFDAberta,
    //view.tabNotaDivergente,
    view.tabNotaPedido,
    // view.tabNotaNFD,
    view.tabNotaColeta,
    view.tabNotaColetaRep,
    // view.tabPedidoGarantia,
    // view.tabNotaGarantia,
    view.tabNotaTransportadora,
    view.tabNotaEmail,
    view.tabNotaRetornoNFD,
    view.tabNotaReposto,
    view.tabNotaAcerto,
    view.tabNotaAcertoPago,
    view.tabNotaAjuste,
    view.tabNotaDescarte,
    view.tabNotaNulo,
    view.tabNotaEditor,
    view.tabNotaFornecedor,
    view.tabNotaNFDSTNR,
    view.tabNotaUsr,
  )
}

interface IDevFor2View : IView {
  val tabNotaEntrada: ITabNotaEntrada
  val tabNotaEditor: ITabNotaEditor
  val tabNotaDivergente: ITabNotaDivergente
  val tabNotaColetaRep: ITabNotaColetaRep
  val tabNotaPedido: ITabNotaPedido
  val tabNotaNFDSTNR: ITabNotaNFDSTNR
  val tabNotaColeta: ITabNotaColeta
  val tabNotaNFD: ITabNotaNFD
  val tabNotaNFDAberta: ITabNotaNFDAberta
  val tabPedidoGarantia: ITabPedidoGarantia
  val tabNotaGarantia: ITabNotaGarantia
  val tabNotaTransportadora: ITabNotaTransportadora
  val tabNotaEmail: ITabNotaEmail
  val tabNotaRetornoNFD: ITabNotaRetornoNFD
  val tabNotaReposto: ITabNotaReposto
  val tabNotaAcerto: ITabNotaAcerto
  val tabNotaAcertoPago: ITabNotaAcertoPago
  val tabNotaAjuste: ITabNotaAjuste
  val tabNotaDescarte: ITabNotaDescarte
  val tabNotaNulo: ITabNotaNulo
  val tabNotaFornecedor: ITabNotaFornecedor
  val tabNotaUsr: ITabNotaUsr
}

