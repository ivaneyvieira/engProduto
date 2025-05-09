package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.produto.viewmodel.devFor2.TabNotaEntradaViewModel

class RecebimentoViewModel(view: IRecebimentoView) : ViewModel<IRecebimentoView>(view) {
  val tabPedidoViewModel = TabPedidoViewModel(this)
  val tabAgendaViewModel = TabAgendaViewModel(this)
  val tabRecebimentoXmlViewModel = TabRecebimentoXmlViewModel(this)
  val tabRecebimentoPreEntViewModel = TabRecebimentoPreEntViewModel(this)
  val tabReceberNotaViewModel = TabReceberNotaViewModel(this)
  val tabValidadeViewModel = TabValidadeViewModel(this)
  val tabNotaRecebidaViewModel = TabNotaRecebidaViewModel(this)
  val tabRecebimentoUsrViewModel = TabRecebimentoUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabPedido,
    view.tabAgenda,
    view.tabRecebimentoXml,
    view.tabRecebimentoPreEnt,
    view.tabReceberNota,
    view.tabValidade,
    view.tabNotaRecebida,
    view.tabRecebimentoUsr,
  )
}

interface IRecebimentoView : IView {
  val tabPedido: ITabPedido
  val tabAgenda: ITabAgenda
  val tabReceberNota: ITabReceberNota
  val tabValidade: ITabValidade
  val tabNotaRecebida: ITabNotaRecebida
  val tabRecebimentoUsr: ITabRecebimentoUsr
  val tabRecebimentoXml: ITabRecebimentoXML
  val tabRecebimentoPreEnt: ITabRecebimentoPreEnt
}

