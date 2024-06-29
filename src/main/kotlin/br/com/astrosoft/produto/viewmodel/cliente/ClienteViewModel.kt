package br.com.astrosoft.produto.viewmodel.cliente

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ClienteViewModel(view: IClienteView) : ViewModel<IClienteView>(view) {
  val tabCaastroViewModel = TabCadastroViewModel(this)

  override fun listTab() = listOf(
    view.tabCadastro,
  )
}

interface IClienteView : IView {
  val tabCadastro: ITabCadastro
}