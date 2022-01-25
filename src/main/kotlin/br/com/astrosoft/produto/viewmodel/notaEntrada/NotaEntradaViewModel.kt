package br.com.astrosoft.produto.viewmodel.notaEntrada

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.produto.viewmodel.notaSaida.*

class NotaEntradaViewModel(view: INotaEntradaView) : ViewModel<INotaEntradaView>(view) {
  val tabNotaReceberViewModel = TabNotaEntradaReceberViewModel(this)

  override fun listTab() = listOf(view.tabNotaEntradaReceber)
}

interface INotaEntradaView : IView {
  val tabNotaEntradaReceber: ITabNotaEntradaReceber
}

