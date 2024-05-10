package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class VendaRefViewModel(view: IVendaRefView) : ViewModel<IVendaRefView>(view) {
  val tabVendaRefViewModel = TabVendaRefViewModel(this)

  override fun listTab() = listOf(
    view.tabVendaRef,
  )
}

interface IVendaRefView : IView {
  val tabVendaRef: ITabVendaRef
}

