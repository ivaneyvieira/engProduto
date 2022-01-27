package br.com.astrosoft.produto.viewmodel.notaEntrada

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class NotaEntradaViewModel(view: INotaEntradaView) : ViewModel<INotaEntradaView>(view) {
  val tabNotaReceberViewModel = TabNotaEntradaReceberViewModel(this)
  val tabNotaPendenteViewModel = TabNotaEntradaPendenteViewModel(this)
  val tabNotaRecebidoViewModel = TabNotaEntradaRecebidoViewModel(this)

  override fun listTab() = listOf(view.tabNotaEntradaReceber, view.tabNotaEntradaPendente, view.tabNotaEntradaRecebido)
}

interface INotaEntradaView : IView {
  val tabNotaEntradaReceber: ITabNotaEntradaReceber
  val tabNotaEntradaPendente: ITabNotaEntradaPendente
  val tabNotaEntradaRecebido: ITabNotaEntradaRecebido
}

