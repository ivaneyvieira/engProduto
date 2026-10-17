package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class RessuprimentoViewModel(view: IRessuprimentoView) : ViewModel<IRessuprimentoView>(view) {
  val tabRessuprimentoRessupViewModel = TabRessuprimentoRessupViewModel(this)
  val tabPedidoRessuprimentoViewModel = TabPedidoRessuprimentoViewModel(this)
  val tabRessuprimentoCDViewModel = TabRessuprimentoCDViewModel(this)
  val tabRessuprimentoTransViewModel = TabRessuprimentoTransViewModel(this)
  val tabRessuprimentoReceberViewModel = TabRessuprimentoReceberViewModel(this)
  val tabRessuprimentoPenViewModel = TabRessuprimentoPenViewModel(this)
  val tabRessuprimentoRecebidoViewModel = TabRessuprimentoRecebidoViewModel(this)
  val tabRessuprimentoUsrViewModel = TabRessuprimentoUsrViewModel(this)
  
  override fun listTab() = listOf(
    view.tabRessuprimentoRessup,
    view.tabPedidoRessuprimento,
    view.tabRessuprimentoCD,
    view.tabRessuprimentoTrans,
    view.tabRessuprimentoReceber,
    view.tabRessuprimentoPen,
    view.tabRessuprimentoRecebido,
    view.tabRessuprimentoUsr,
  )
}

interface IRessuprimentoView : IView {
  val tabRessuprimentoRessup: ITabRessuprimentoRessup
  val tabPedidoRessuprimento: ITabPedidoRessuprimento
  val tabRessuprimentoCD: ITabRessuprimentoCD
  val tabRessuprimentoTrans: ITabRessuprimentoTrans
  val tabRessuprimentoReceber: ITabRessuprimentoReceber
  val tabRessuprimentoPen: ITabRessuprimentoPen
  val tabRessuprimentoRecebido: ITabRessuprimentoRecebido
  val tabRessuprimentoUsr: ITabRessuprimentoUsr
}

