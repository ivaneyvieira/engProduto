package br.com.astrosoft.produto.viewmodel.notaSaida

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class NotaViewModel(view: INotaView) : ViewModel<INotaView>(view) {
  val tabNotaSepViewModel = TabNotaSepViewModel(this)
  val tabNotaRotaViewModel = TabNotaRotaViewModel(this)
  val tabNotaExpViewModel = TabNotaExpViewModel(this)
  val tabNotaCDViewModel = TabNotaCDViewModel(this)
  val tabNotaEntViewModel = TabNotaEntViewModel(this)
  val tabNotaUsrViewModel = TabNotaUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabNotaSep,
    view.tabNotaRota,
    view.tabNotaExp,
    view.tabNotaCD,
    view.tabNotaEnt,
    view.tabNotaUsr,
  )
}

interface INotaView : IView {
  val tabNotaSep: ITabNotaSep
  val tabNotaRota: ITabNotaRota
  val tabNotaExp: ITabNotaExp
  val tabNotaCD: ITabNotaCD
  val tabNotaEnt: ITabNotaEnt
  val tabNotaUsr: ITabNotaUsr
}

