package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class RessuprimentoViewModel(view: IRessuprimentoView) : ViewModel<IRessuprimentoView>(view) {
  val tabRessuprimentoRessupViewModel = TabRessuprimentoRessupViewModel(this)
  val tabPedidoRessuprimentoViewModel = TabPedidoRessuprimentoViewModel(this)
  val tabRessuprimentoCDViewModel = TabRessuprimentoCDViewModel(this)
  val tabRessuprimentoSepViewModel = TabRessuprimentoSepViewModel(this)
  val tabRessuprimentoEntViewModel = TabRessuprimentoEntViewModel(this)
  val tabRessuprimentoPenViewModel = TabRessuprimentoPenViewModel(this)
  val tabRessuprimentoRecViewModel = TabRessuprimentoRecViewModel(this)
  val tabRessuprimentoUsrViewModel = TabRessuprimentoUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabRessuprimentoRessup,
    view.tabPedidoRessuprimento,
    view.tabRessuprimentoCD,
    view.tabRessuprimentoSep,
    view.tabRessuprimentoEnt,
    view.tabRessuprimentoPen,
    view.tabRessuprimentoRec,
    view.tabRessuprimentoUsr,
  )
}

interface IRessuprimentoView : IView {
  val tabRessuprimentoRessup: ITabRessuprimentoRessup
  val tabPedidoRessuprimento: ITabPedidoRessuprimento
  val tabRessuprimentoCD: ITabRessuprimentoCD
  val tabRessuprimentoSep: ITabRessuprimentoSep
  val tabRessuprimentoEnt: ITabRessuprimentoEnt
  val tabRessuprimentoPen: ITabRessuprimentoPen
  val tabRessuprimentoRec: ITabRessuprimentoRec
  val tabRessuprimentoUsr: ITabRessuprimentoUsr
}

