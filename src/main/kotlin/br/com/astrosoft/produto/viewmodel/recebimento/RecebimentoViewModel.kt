package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class RecebimentoViewModel(view: IRecebimentoView) : ViewModel<IRecebimentoView>(view) {
  val tabPedidoViewModel = TabPedidoViewModel(this)
  val tabPreEntradaViewModel = TabPreEntradaViewModel(this)
  val tabRecebimentoXmlViewModel = TabRecebimentoXmlViewModel(this)
  val tabRecebimentoPreEntViewModel = TabRecebimentoPreEntViewModel(this)
  val tabRecebeNotaViewModel = TabRecebeNotaViewModel(this)
  val tabValidadeViewModel = TabValidadeViewModel(this)
  val tabNotaRecebidaViewModel = TabNotaRecebidaViewModel(this)
  val tabRecebimentoUsrViewModel = TabRecebimentoUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabPedido,
    view.tabPreEntrada,
    view.tabRecebimentoXml,
    view.tabRecebimentoPreEntXml,
    view.tabRecebeNota,
    view.tabValidade,
    view.tabNotaRecebida,
    view.tabRecebimentoUsr,
  )
}

interface IRecebimentoView : IView {
  val tabPedido: ITabPedido
  val tabPreEntrada: ITabPreEntrada
  val tabRecebeNota: ITabReceberNota
  val tabValidade: ITabValidade
  val tabNotaRecebida: ITabNotaRecebida
  val tabRecebimentoUsr: ITabRecebimentoUsr
  val tabRecebimentoXml: ITabRecebimentoXML
  val tabRecebimentoPreEntXml: ITabRecebimentoPreEntXml
}

