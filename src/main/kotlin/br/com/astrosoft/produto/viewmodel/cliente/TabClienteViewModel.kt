package br.com.astrosoft.produto.viewmodel.cliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.DadosCliente
import br.com.astrosoft.produto.model.beans.FiltroDadosCliente

class TabClienteViewModel(val viewModel: ClienteViewModel) {
  val subView
    get() = viewModel.view.tabCliente

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = DadosCliente.findAll(filtro)
    subView.updateNotas(notas)
  }
}

interface ITabCliente : ITabView {
  fun filtro(): FiltroDadosCliente
  fun updateNotas(movManualList: List<DadosCliente>)
}