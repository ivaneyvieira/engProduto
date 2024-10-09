package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabValidadeListViewModel

class RecebimentoViewModel(view: IRecebimentoView) : ViewModel<IRecebimentoView>(view) {
  val tabAgendaViewModel = TabAgendaViewModel(this)
  val tabReceberViewModel = TabReceberViewModel(this)
  val tabDevClientesViewModel = TabDevClientesViewModel(this)
  val tabReclassificaViewModel = TabReclassificaViewModel(this)
  val tabReclassRecViewModel = TabReclassRecViewModel(this)
  val tabDevCliRecViewModel = TabDevCliRecViewModel(this)
  val tabTransferenciaViewModel = TabTransferenciaViewModel(this)
  val tabReceberNotaViewModel = TabReceberNotaViewModel(this)
  val tabNotaRecebidaViewModel = TabNotaRecebidaViewModel(this)
  val tabRecebidoViewModel = TabRecebidoViewModel(this)
  val tabTransfRecebViewModel = TabTransfRecebViewModel(this)
  val tabRecebimentoUsrViewModel = TabRecebimentoUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabAgenda,
    view.tabReceberNota,
    view.tabNotaRecebida,
    //view.tabReceber,
    //view.tabDevClientes,
    //view.tabReclassifica,
    //view.tabTransferencia,
    //view.tabRecebido,
    //view.tabDevCliRec,
    //view.tabReclassRec,
    //view.tabTransfReceb,
    //view.tabRecebimentoUsr,
  )
}

interface IRecebimentoView : IView {
  val tabAgenda: ITabAgenda
  val tabReceber: ITabReceber
  val tabReclassifica: ITabReclassifica
  val tabReclassRec: ITabReclassRec
  val tabDevClientes: ITabDevClientes
  val tabDevCliRec: ITabDevCliRec
  val tabTransferencia: ITabTransferencia
  val tabReceberNota: ITabReceberNota
  val tabRecebido: ITabRecebido
  val tabTransfReceb: ITabTransfReceb
  val tabNotaRecebida: ITabNotaRecebida
  val tabRecebimentoUsr: ITabRecebimentoUsr
}

