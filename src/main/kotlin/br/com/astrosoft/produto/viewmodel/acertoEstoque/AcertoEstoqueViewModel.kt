package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.produto.viewmodel.devCliente.*

class AcertoEstoqueViewModel(view: IAcertoEstoqueView) : ViewModel<IAcertoEstoqueView>(view) {
  val tabAcertoPedidoViewModel = TabAcertoPedidoViewModel(this)
  val tabAcertoEstoqueEntradaViewModel = TabAcertoEstoqueEntradaViewModel(this)
  val tabAcertoEstoqueSaidaViewModel = TabAcertoEstoqueSaidaViewModel(this)
  val tabAcertoMovManualSaidaViewModel = TabAcertoMovManualSaidaViewModel(this)
  val tabAcertoMovManualEntradaViewModel = TabAcertoMovManualEntradaViewModel(this)
  val tabAcertoMovAtacadoViewModel = TabAcertoMovAtacadoViewModel(this)
  val tabAcertoUsrViewModel = TabAcertoUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabAcertoPedido,
    view.tabAcertoEstoqueEntrada,
    view.tabAcertoEstoqueSaida,
    view.tabAcertoMovManualEntrada,
    view.tabAcertoMovManualSaida,
    view.tabAcertoMovAtacado,
    view.tabAcertoUsr,
  )
}

interface IAcertoEstoqueView : IView {
  val tabAcertoPedido: ITabAcertoPedido
  val tabAcertoEstoqueEntrada: ITabAcertoEstoqueEntrada
  val tabAcertoEstoqueSaida: ITabAcertoEstoqueSaida
  val tabAcertoMovManualSaida: ITabAcertoMovManualSaida
  val tabAcertoMovManualEntrada: ITabAcertoMovManualEntrada
  val tabAcertoMovAtacado: ITabAcertoMovAtacado
  val tabAcertoUsr: ITabAcertoUsr
}