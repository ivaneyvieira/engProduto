package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.produto.viewmodel.devCliente.*

class AcertoEstoqueViewModel(view: IAcertoEstoqueView) : ViewModel<IAcertoEstoqueView>(view) {
  val tabAcertoEstoqueEntradaViewModel = TabAcertoEstoqueEntradaViewModel(this)
  val tabAcertoEstoqueSaidaViewModel = TabAcertoEstoqueSaidaViewModel(this)
  val tabAcertoMovManualSaidaViewModel = TabAcertoMovManualSaidaViewModel(this)
  val tabAcertoMovManualEntradaViewModel = TabAcertoMovManualEntradaViewModel(this)
  val tabAcertoMovAtacadoViewModel = TabAcertoMovAtacadoViewModel(this)

  override fun listTab() = listOf(
    view.tabAcertoEstoqueEntrada,
    view.tabAcertoEstoqueSaida,
    view.tabAcertoMovManualEntrada,
    view.tabAcertoMovManualSaida,
    view.tabAcertoMovAtacado,
  )
}

interface IAcertoEstoqueView : IView {
  val tabAcertoEstoqueEntrada: ITabAcertoEstoqueEntrada
  val tabAcertoEstoqueSaida: ITabAcertoEstoqueSaida
  val tabAcertoMovManualSaida: ITabAcertoMovManualSaida
  val tabAcertoMovManualEntrada: ITabAcertoMovManualEntrada
  val tabAcertoMovAtacado: ITabAcertoMovAtacado
}