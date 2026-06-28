package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class VendaRefViewModel(view: IVendaRefView) : ViewModel<IVendaRefView>(view) {
  val tabVendaRefViewModel = TabVendaRefViewModel(this)
  val tabVendaDetViewModel = TabVendaDetViewModel(this)
  val tabResumoViewModel = TabResumoViewModel(this)
  val tabResumoPgtoViewModel = TabResumoPgtoViewModel(this)
  val tabResumoTipoViewModel = TabResumoTipoViewModel(this)
  val tabResumoCartaoViewModel = TabResumoCartaoViewModel(this)
  val tabVendaRefUsrViewModel = VendaRefUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabVendaRef,
    view.tabVendaDet,
    view.tabResumo,
    view.tabResumoPgto,
    view.tabResumoTipo,
    view.tabResumoCartao,
    view.tabVendaRefUsr,
  )
}

interface IVendaRefView : IView {
  val tabVendaRef: ITabVendaRef
  val tabVendaDet: ITabVendaDet
  val tabResumo: ITabResumo
  val tabResumoPgto: ITabResumoPgto
  val tabResumoTipo: ITabResumoTipo
  val tabResumoCartao: ITabResumoCartao
  val tabVendaRefUsr: ITabVendaRefUsr
}

