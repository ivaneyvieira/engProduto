package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroFornecedorLoja
import br.com.astrosoft.produto.model.beans.FornecedorLoja

class TabEstoqueFornViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabEstoqueForn

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val list = FornecedorLoja.findAll(filtro)
    subView.updateGrid(list)
  }

  fun saveForn(bean: FornecedorLoja?) = viewModel.exec {
    bean ?: fail("Nenhum produto selecionado")
    bean.update()
    updateView()
  }
}

interface ITabEstoqueForn : ITabView {
  fun filtro(): FiltroFornecedorLoja
  fun updateGrid(fornecedores: List<FornecedorLoja>)
}
