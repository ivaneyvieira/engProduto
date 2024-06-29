package br.com.astrosoft.produto.viewmodel.cliente

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel

class ClienteViewModel(view: IClienteView) : ViewModel<IClienteView>(view) {
  val tabCadastroViewModel = TabCadastroViewModel(this)
  val tabClienteUsrViewModel = TabClienteUsrViewModel(this)

  override fun listTab() = listOf(
    view.tabCadastro,
    view.tabClienteUsr,
  )
}

interface IClienteView : IView {
  val tabCadastro: ITabCadastro
  val tabClienteUsr: ITabClienteUsr
}