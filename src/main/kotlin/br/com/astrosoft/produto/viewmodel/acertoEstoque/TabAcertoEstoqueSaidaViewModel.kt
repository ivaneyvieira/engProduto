package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*

class TabAcertoEstoqueSaidaViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoEstoqueSaida

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = AcertoSaida.findAll(filtro)
    subView.updateClientes(notas)
  }
}

interface ITabAcertoEstoqueSaida : ITabView {
  fun filtro(): FiltroAcertoSaida
  fun updateClientes(clientes: List<AcertoSaidaNota>)
}