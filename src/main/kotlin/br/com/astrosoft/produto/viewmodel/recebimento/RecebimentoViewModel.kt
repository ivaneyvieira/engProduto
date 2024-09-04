package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class RecebimentoViewModel(view: IRecebimentoView) : ViewModel<IRecebimentoView>(view) {
  val tabReceberViewModel = TabReceberViewModel(this)
  val tabDevClientesViewModel = TabDevClientesViewModel(this)
  val tabTransferenciaViewModel = TabTransferenciaViewModel(this)
  val tabRecebidoViewModel = TabRecebidoViewModel(this)
  val tabTransfRecebViewModel = TabTransfRecebViewModel(this)
  val tabValidadeListViewModel = TabValidadeListViewModel(this)
  val tabRecebimentoUsrViewModel = TabRecebimentoUsrViewModel(this)


  override fun listTab() = listOf(
    view.tabReceber,
    view.tabDevClientes,
    view.tabTransferencia,
    view.tabRecebido,
    view.tabTransfReceb,
    view.tabValidadeList,
    view.tabRecebimentoUsr
    )
}

interface IRecebimentoView : IView {
  val tabReceber: ITabReceber
  val tabDevClientes: ITabDevClientes
  val tabTransferencia: ITabTransferencia
  val tabRecebido: ITabRecebido
  val tabTransfReceb: ITabTransfReceb
  val tabValidadeList: ITabValidadeList
  val tabRecebimentoUsr: ITabRecebimentoUsr
}

