package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevClienteViewModel(view: IDevClienteView) : ViewModel<IDevClienteView>(view) {
  val tabDevCliEditorViewModel = TabDevCliEditorViewModel(this)
  val tabDevCliSemPrdViewModel = TabDevCliSemPrdViewModel(this)
  val tabDevCliValeTrocaViewModel = TabDevCliValeTrocaViewModel(this)
  val tabDevCliDevTrocaViewModel = TabDevCliDevTrocaViewModel(this)
  val tabDevCliValeTrocaProdutoViewModel = TabDevCliValeTrocaProdutoViewModel(this)
  val tabDevCliComPrdViewModel = TabDevCliComPrdViewModel(this)
  val tabDevCliCreditoViewModel = TabDevCliCreditoViewModel(this)
  val tabDevVendaViewModel = TabDevVendaViewModel(this)

  override fun listTab() = listOf(
    view.tabDevCliEditor,
    view.tabDevCliValeTroca,
    view.tabDevCliComPrd,
    view.tabDevCliSemPrd,
    view.tabDevCliValeTrocaProduto,
    view.tabDevCliCredito,
    view.tabDevCliDevTroca,
    view.tabDevVenda,
  )
}

interface IDevClienteView : IView {
  val tabDevCliValeTroca: ITabDevCliValeTroca
  val tabDevCliDevTroca: ITabDevCliDevTroca
  val tabDevCliEditor: ITabDevCliEditor
  val tabDevCliSemPrd: ITabDevCliSemPrd
  val tabDevCliValeTrocaProduto: ITabDevCliValeTrocaProduto
  val tabDevCliComPrd: ITabDevCliComPrd
  val tabDevCliCredito: ITabDevCliCredito
  val tabDevVenda: ITabDevVenda
}