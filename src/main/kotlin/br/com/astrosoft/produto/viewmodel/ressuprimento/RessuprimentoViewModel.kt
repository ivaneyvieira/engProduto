package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class RessuprimentoViewModel(view: IRessuprimentoView) : ViewModel<IRessuprimentoView>(view) {
  val tabRessuprimentoCDViewModel = TabRessuprimentoCDViewModel(this)
  val tabRessuprimentoEntViewModel = TabRessuprimentoEntViewModel(this)

  override fun listTab() = listOf(view.tabRessuprimentoCD, view.tabRessuprimentoEnt)
}

interface IRessuprimentoView : IView {
  val tabRessuprimentoCD: ITabRessuprimentoCD
  val tabRessuprimentoEnt: ITabRessuprimentoEnt
}

