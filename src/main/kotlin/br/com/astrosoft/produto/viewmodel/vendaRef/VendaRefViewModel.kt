package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class VendaRefViewModel(view: IVendaRefView) : ViewModel<IVendaRefView>(view) {
  val tabVendaRefViewModel = TabVendaRefViewModel(this)
  val tabResumoViewModel = TabResumoViewModel(this)

  override fun listTab() = listOf(
    view.tabVendaRef,
    view.tabResumo,
  )
}

interface IVendaRefView : IView {
  val tabVendaRef: ITabVendaRef
  val tabResumo: ITabResumo
}

