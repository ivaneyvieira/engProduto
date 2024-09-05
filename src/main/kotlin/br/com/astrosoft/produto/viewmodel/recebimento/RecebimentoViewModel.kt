package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class RecebimentoViewModel(view: IRecebimentoView) : ViewModel<IRecebimentoView>(view) {
  val tabReceberViewModel = TabReceberViewModel(this)
  val tabDevClientesViewModel = TabDevClientesViewModel(this)
  val tabReclassificaViewModel = TabReclassificaViewModel(this)
  val tabReclassRecViewModel = TabReclassRecViewModel(this)
  val tabDevCliRecViewModel = TabDevCliRecViewModel(this)
  val tabTransferenciaViewModel = TabTransferenciaViewModel(this)
  val tabRecebidoViewModel = TabRecebidoViewModel(this)
  val tabTransfRecebViewModel = TabTransfRecebViewModel(this)
  val tabValidadeListViewModel = TabValidadeListViewModel(this)
  val tabRecebimentoUsrViewModel = TabRecebimentoUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabReceber,
    view.tabDevClientes,
    view.tabReclassifica,
    view.tabTransferencia,
    view.tabRecebido,
    view.tabDevCliRec,
    view.tabReclassRec,
    view.tabTransfReceb,
    view.tabValidadeList,
    view.tabRecebimentoUsr,
  )
}

interface IRecebimentoView : IView {
  val tabReceber: ITabReceber
  val tabReclassifica: ITabReclassifica
  val tabReclassRec: ITabReclassRec
  val tabDevClientes: ITabDevClientes
  val tabDevCliRec: ITabDevCliRec
  val tabTransferencia: ITabTransferencia
  val tabRecebido: ITabRecebido
  val tabTransfReceb: ITabTransfReceb
  val tabValidadeList: ITabValidadeList
  val tabRecebimentoUsr: ITabRecebimentoUsr
}

