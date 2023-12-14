package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.CreditoCliente
import br.com.astrosoft.produto.model.beans.FiltroCreditoCliente
import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.textfield.TextField

class TabDevCliCreditoViewModel(val viewModel: DevClienteViewModel) {
  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val clientes = CreditoCliente.findCreditoCliente(filtro)
    subView.updateClientes(clientes)
  }

  val subView
    get() = viewModel.view.tabDevCliCredito
}

interface ITabDevCliCredito : ITabView {
  fun filtro(): FiltroCreditoCliente
  fun updateClientes(clientes: List<CreditoCliente>)
}