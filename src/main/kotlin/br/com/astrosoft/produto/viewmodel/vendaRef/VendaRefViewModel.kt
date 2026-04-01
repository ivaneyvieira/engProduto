package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class VendaRefViewModel(view: IVendaRefView) : ViewModel<IVendaRefView>(view) {
  val tabVendaRefViewModel = TabVendaRefViewModel(this)
  val tabResumoViewModel = TabResumoViewModel(this)
  val tabResumoPgtoViewModel = TabResumoPgtoViewModel(this)
  val tabResumoCartaoViewModel = TabResumoCartaoViewModel(this)
  val tabVendaRefUsrViewModel = VendaRefUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabVendaRef,
    view.tabResumo,
    view.tabResumoPgto,
    view.tabResumoCartao,
    view.tabVendaRefUsr,
  )
}

interface IVendaRefView : IView {
  val tabVendaRef: ITabVendaRef
  val tabResumo: ITabResumo
  val tabResumoPgto: ITabResumoPgto
  val tabResumoCartao: ITabResumoCartao
  val tabVendaRefUsr: ITabVendaRefUsr
}

