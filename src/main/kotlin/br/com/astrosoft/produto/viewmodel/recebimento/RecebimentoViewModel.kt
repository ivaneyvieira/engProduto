package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class RecebimentoViewModel(view: IRecebimentoView) : ViewModel<IRecebimentoView>(view) {
  val tabPedidoViewModel = TabPedidoViewModel(this)
  val tabAgendaViewModel = TabAgendaViewModel(this)
  val tabFileNFEViewModel = TabFileNFEViewModel(this)
  val tabReceberNotaViewModel = TabReceberNotaViewModel(this)
  val tabNotaRecebidaViewModel = TabNotaRecebidaViewModel(this)
  val tabRecebimentoUsrViewModel = TabRecebimentoUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabPedido,
    view.tabAgenda,
    view.tabFileNFE,
    view.tabReceberNota,
    view.tabNotaRecebida,
    view.tabRecebimentoUsr,
  )
}

interface IRecebimentoView : IView {
  val tabPedido: ITabPedido
  val tabAgenda: ITabAgenda
  val tabReceberNota: ITabReceberNota
  val tabNotaRecebida: ITabNotaRecebida
  val tabRecebimentoUsr: ITabRecebimentoUsr
  val tabFileNFE: ITabFileNFEViewModel
}

