package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class RessuprimentoViewModel(view: IRessuprimentoView) : ViewModel<IRessuprimentoView>(view) {
  val tabRessuprimentoCDViewModel = TabRessuprimentoCDViewModel(this)
  val tabRessuprimentoSepViewModel = TabRessuprimentoSepViewModel(this)
  val tabRessuprimentoEntViewModel = TabRessuprimentoEntViewModel(this)
  val tabRessuprimentoRecViewModel = TabRessuprimentoRecViewModel(this)

  override fun listTab() = listOf(
    view.tabRessuprimentoCD,
    view.tabRessuprimentoSep,
    view.tabRessuprimentoEnt,
    view.tabRessuprimentoRec,
    )
}

interface IRessuprimentoView : IView {
  val tabRessuprimentoCD: ITabRessuprimentoCD
  val tabRessuprimentoSep: ITabRessuprimentoSep
  val tabRessuprimentoEnt: ITabRessuprimentoEnt
  val tabRessuprimentoRec: ITabRessuprimentoRec
}

