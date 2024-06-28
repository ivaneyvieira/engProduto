package br.com.astrosoft.produto.viewmodel.cliente

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.produto.viewmodel.acertoEstoque.*
import br.com.astrosoft.produto.viewmodel.devCliente.*

class ClienteViewModel(view: IClienteView) : ViewModel<IClienteView>(view) {
  val tabClienteViewModel = TabClienteViewModel(this)

  override fun listTab() = listOf(
    view.tabCliente,
  )
}

interface IClienteView : IView {
  val tabCliente: ITabCliente
}