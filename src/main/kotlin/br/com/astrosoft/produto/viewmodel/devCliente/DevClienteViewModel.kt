package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevClienteViewModel(view: IDevClienteView) : ViewModel<IDevClienteView>(view) {
  val tabDevCliEditorViewModel = TabDevCliEditorViewModel(this)
  val tabDevCliImprimirViewModel = TabDevCliImprimirViewModel(this)
  val tabDevCliDevTrocaViewModel = TabDevCliDevTrocaViewModel(this)
  val tabDevCliValeTrocaProdutoViewModel = TabDevCliValeTrocaProdutoViewModel(this)
  val tabDevCliImpressoViewModel = TabDevCliImpressoViewModel(this)
  val tabDevCliCreditoViewModel = TabDevCliCreditoViewModel(this)
  val tabDevCliVendaViewModel = TabDevCliVendaViewModel(this)
  val tabDevCliUsrViewModel = TabDevCliUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabDevCliEditor,
    view.tabDevCliImprimir,
    view.tabDevCliImpresso,
    view.tabDevCliValeTrocaProduto,
    view.tabDevCliCredito,
    view.tabDevCliDevTroca,
    view.tabDevCliVenda,
    view.tabDevCliUsr,
  )
}

interface IDevClienteView : IView {
  val tabDevCliImprimir: ITabDevCliImprimir
  val tabDevCliDevTroca: ITabDevCliDevTroca
  val tabDevCliEditor: ITabDevCliEditor
  val tabDevCliValeTrocaProduto: ITabDevCliValeTrocaProduto
  val tabDevCliImpresso: ITabDevCliImpresso
  val tabDevCliCredito: ITabDevCliCredito
  val tabDevCliVenda: ITabDevVenda
  val tabDevCliUsr: ITabDevCliUsr
}