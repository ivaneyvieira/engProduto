package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevClienteViewModel(view: IDevClienteView) : ViewModel<IDevClienteView>(view) {
  val tabDevCliValeTrocaViewModel = TabDevCliValeTrocaViewModel(this)
  val tabDevCliValeTrocaProdutoViewModel = TabDevCliValeTrocaProdutoViewModel(this)
  val tabDevCliValeTrocaImpViewModel = TabDevCliValeTrocaImpViewModel(this)
  val tabDevCliCreditoViewModel = TabDevCliCreditoViewModel(this)

  override fun listTab() = listOf(
    view.tabDevCliValeTroca, view.tabDevCliValeTrocaImp, view.tabDevCliValeTrocaProduto, view.tabDevCliCredito,
  )
}

interface IDevClienteView : IView {
  val tabDevCliValeTroca: ITabDevCliValeTroca
  val tabDevCliValeTrocaProduto: ITabDevCliValeTrocaProduto
  val tabDevCliValeTrocaImp: ITabDevCliValeTrocaImp
  val tabDevCliCredito: ITabDevCliCredito
}