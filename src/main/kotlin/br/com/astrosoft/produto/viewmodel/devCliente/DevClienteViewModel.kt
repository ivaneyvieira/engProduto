package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevClienteViewModel(view: IDevClienteView) : ViewModel<IDevClienteView>(view) {
  val tabDevCliEditorViewModel = TabDevCliEditorViewModel(this)
  val tabDevCliImprimirViewModel = TabDevCliImprimirViewModel(this)
  val tabDevCliDevolucoesViewModel = TabDevCliDevolucoesViewModel(this)
  val tabDevCliDevTrocaViewModel = TabDevCliDevTrocaViewModel(this)
  val tabDevCliProdutoViewModel = TabDevCliProdutoViewModel(this)
  val tabDevCliImpressoViewModel = TabDevCliImpressoViewModel(this)
  val tabDevCliCreditoViewModel = TabDevCliCreditoViewModel(this)
  val tabDevAutorizaViewModel = TabDevAutorizaViewModel(this)
  val tabDevCancelaViewModel = TabDevCancelaViewModel(this)
  val tabDevCliVendaViewModel = TabDevCliVendaViewModel(this)
  val tabDevCliUsrViewModel = TabDevCliUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabDevAutoriza,
    view.tabDevCliDevolucoes,
    view.tabDevCliImprimir,
    view.tabDevCliImpresso,
    view.tabDevCancela,
    view.tabDevCliEditor,
    view.tabDevCliProduto,
    view.tabDevCliCredito,
    view.tabDevCliDevTroca,
    view.tabDevCliVenda,
    view.tabDevCliUsr,
  )
}

interface IDevClienteView : IView {
  val tabDevCliImprimir: ITabDevCliImprimir
  val tabDevCliDevolucoes: ITabDevCliDevolucoes
  val tabDevCliDevTroca: ITabDevCliDevTroca
  val tabDevCliEditor: ITabDevCliEditor
  val tabDevCliProduto: ITabDevCliProduto
  val tabDevCliImpresso: ITabDevCliImpresso
  val tabDevCliCredito: ITabDevCliCredito
  val tabDevAutoriza: ITabDevAutoriza
  val tabDevCancela: ITabDevCancela
  val tabDevCliVenda: ITabDevVenda
  val tabDevCliUsr: ITabDevCliUsr
}