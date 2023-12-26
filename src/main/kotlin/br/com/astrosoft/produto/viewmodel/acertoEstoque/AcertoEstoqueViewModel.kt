package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.produto.viewmodel.devCliente.*

class AcertoEstoqueViewModel(view: IAcertoEstoqueView) : ViewModel<IAcertoEstoqueView>(view) {
  val tabAcertoEstoqueEntradaViewModel = TabAcertoEstoqueEntradaViewModel(this)
  val tabAcertoEstoqueSaidaViewModel = TabAcertoEstoqueSaidaViewModel(this)

  override fun listTab() = listOf(
    view.tabAcertoEstoqueEntrada,
    view.tabAcertoEstoqueSaida,
  )
}

interface IAcertoEstoqueView : IView {
  val tabAcertoEstoqueEntrada: ITabAcertoEstoqueEntrada
  val tabAcertoEstoqueSaida: ITabAcertoEstoqueSaida
}