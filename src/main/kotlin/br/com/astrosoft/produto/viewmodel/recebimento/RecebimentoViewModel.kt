package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class RecebimentoViewModel(view: IRecebimentoView) : ViewModel<IRecebimentoView>(view) {
  val tabReceberViewModel = TabReceberViewModel(this)
  val tabRecebidoViewModel = TabRecebidoViewModel(this)
  val tabRecebimentoUsrViewModel = TabRecebimentoUsrViewModel(this)


  override fun listTab() = listOf(
    view.tabReceber,
    view.tabRecebido,
    view.tabRecebimentoUsr
    )
}

interface IRecebimentoView : IView {
  val tabReceber: ITabReceber
  val tabRecebido: ITabRecebido
  val tabRecebimentoUsr: ITabRecebimentoUsr
}

