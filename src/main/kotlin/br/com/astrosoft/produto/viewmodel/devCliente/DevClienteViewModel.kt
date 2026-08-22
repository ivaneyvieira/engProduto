package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class DevClienteViewModel(view: IDevClienteView) : ViewModel<IDevClienteView>(view) {
  val tabDevCliEditorViewModel = TabDevCliEditorViewModel(this)
  val tabDevCliImprimirViewModel = TabDevCliImprimirViewModel(this)
  val tabDevDadosViewModel = TabDevDadosViewModel(this)
  val tabDevDadosImpressoViewModel = TabDevDadosImpressoViewModel(this)
  val tabDevCliDevolucoesViewModel = TabDevCliDevolucoesViewModel(this)
  val tabDevCliDevTrocaViewModel = TabDevCliDevTrocaViewModel(this)
  val tabDevCliProdutoViewModel = TabDevCliProdutoViewModel(this)
  val tabDevDadosProdutoViewModel = TabDevDadosProdutoViewModel(this)
  val tabDevCliImpressoViewModel = TabDevCliImpressoViewModel(this)
  val tabDevCliCreditoViewModel = TabDevCliCreditoViewModel(this)
  val tabDevAutorizaViewModel = TabDevAutorizaViewModel(this)
  val tabDevCancelaViewModel = TabDevCancelaViewModel(this)
  val tabDevCliVendaViewModel = TabDevCliVendaViewModel(this)
  val tabDevCliUsrViewModel = TabDevCliUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabDevDados,
    view.tabDevDadosImpresso,
    view.tabDevDadosProduto,
    view.tabDevCliCredito,
    view.tabDevCancela,
    view.tabDevAutoriza,
    view.tabDevCliImprimir,
    view.tabDevCliImpresso,
    view.tabDevCliDevolucoes,
    view.tabDevCliProduto,
    view.tabDevCliEditor,
    view.tabDevCliDevTroca,
    view.tabDevCliVenda,
    view.tabDevCliUsr,
  )
}

interface IDevClienteView : IView {
  val tabDevCliImprimir: ITabDevCliImprimir
  val tabDevDados: ITabDevDados
  val tabDevDadosImpresso: ITabDevDadosImpresso
  val tabDevCliDevolucoes: ITabDevCliDevolucoes
  val tabDevCliDevTroca: ITabDevCliDevTroca
  val tabDevCliEditor: ITabDevCliEditor
  val tabDevCliProduto: ITabDevCliProduto
  val tabDevDadosProduto: ITabDevDadosProduto
  val tabDevCliImpresso: ITabDevCliImpresso
  val tabDevCliCredito: ITabDevCliCredito
  val tabDevAutoriza: ITabDevAutoriza
  val tabDevCancela: ITabDevCancela
  val tabDevCliVenda: ITabDevVenda
  val tabDevCliUsr: ITabDevCliUsr
}