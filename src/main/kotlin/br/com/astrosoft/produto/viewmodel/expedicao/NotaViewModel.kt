package br.com.astrosoft.produto.viewmodel.expedicao

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class NotaViewModel(view: INotaView) : ViewModel<INotaView>(view) {
  val tabNotaTipoViewModel = TabNotaTipoViewModel(this)
  val tabNotaSepViewModel = TabNotaSepViewModel(this)
  val tabNotaRotaViewModel = TabNotaRotaViewModel(this)
  val tabNotaTrocaViewModel = TabNotaTrocaViewModel(this)
  val tabNotaExpViewModel = TabNotaExpViewModel(this)
  val tabNotaCDViewModel = TabNotaCDViewModel(this)
  val tabNotaEntViewModel = TabNotaEntViewModel(this)
  val tabNotaUsrViewModel = TabNotaUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabNotaTipo,
    view.tabNotaSep,
    view.tabNotaRota,
    view.tabNotaTroca,
    view.tabNotaExp,
    view.tabNotaCD,
    view.tabNotaEnt,
    view.tabNotaUsr,
  )
}

interface INotaView : IView {
  val tabNotaTipo: ITabNotaTipo
  val tabNotaSep: ITabNotaSep
  val tabNotaRota: ITabNotaRota
  val tabNotaTroca: ITabNotaTroca
  val tabNotaExp: ITabNotaExp
  val tabNotaCD: ITabNotaCD
  val tabNotaEnt: ITabNotaEnt
  val tabNotaUsr: ITabNotaUsr
}

