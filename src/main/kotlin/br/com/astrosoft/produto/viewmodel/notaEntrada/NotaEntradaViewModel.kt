package br.com.astrosoft.produto.viewmodel.notaEntrada

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class NotaEntradaViewModel(view: INotaEntradaView) : ViewModel<INotaEntradaView>(view) {
  val tabNotaReceberViewModel = TabNotaEntradaReceberViewModel(this)
  val tabNotaRecebidoViewModel = TabNotaEntradaRecebidoViewModel(this)
  val tabNotaBaseViewModel = TabNotaEntradaBaseViewModel(this)

  override fun listTab() = listOf(view.tabNotaEntradaBase, view.tabNotaEntradaReceber, view.tabNotaEntradaRecebido)
}

interface INotaEntradaView : IView {
  val tabNotaEntradaReceber: ITabNotaEntradaReceber
  val tabNotaEntradaRecebido: ITabNotaEntradaRecebido
  val tabNotaEntradaBase: ITabNotaEntradaBase
}

