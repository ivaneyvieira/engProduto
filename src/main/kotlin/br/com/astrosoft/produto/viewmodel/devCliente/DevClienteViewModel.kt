package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevClienteViewModel(view: IDevClienteView) : ViewModel<IDevClienteView>(view) {
  val tabDevCliEditorViewModel = TabDevCliEditorViewModel(this)
  val tabDevCliAutorizacaoViewModel = TabDevCliAutorizacaoViewModel(this)
  val tabDevCliValeTrocaViewModel = TabDevCliValeTrocaViewModel(this)
  val tabDevCliDevTrocaViewModel = TabDevCliDevTrocaViewModel(this)
  val tabDevCliValeTrocaProdutoViewModel = TabDevCliValeTrocaProdutoViewModel(this)
  val tabDevCliValeTrocaImpViewModel = TabDevCliValeTrocaImpViewModel(this)
  val tabDevCliCreditoViewModel = TabDevCliCreditoViewModel(this)

  override fun listTab() = listOf(
    view.tabDevCliEditor,
    view.tabDevCliValeTroca,
    view.tabDevCliValeTrocaImp,
    view.tabDevCliValeTrocaProduto,
    view.tabDevCliCredito,
    view.tabDevCliDevTroca,
    view.tabDevCliAutorizacao,
  )
}

interface IDevClienteView : IView {
  val tabDevCliValeTroca: ITabDevCliValeTroca
  val tabDevCliDevTroca: ITabDevCliDevTroca
  val tabDevCliEditor: ITabDevCliEditor
  val tabDevCliAutorizacao: ITabDevCliAutorizacao
  val tabDevCliValeTrocaProduto: ITabDevCliValeTrocaProduto
  val tabDevCliValeTrocaImp: ITabDevCliValeTrocaImp
  val tabDevCliCredito: ITabDevCliCredito
}