package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.AcertoEntrada
import br.com.astrosoft.produto.model.beans.AcertoEntradaNota
import br.com.astrosoft.produto.model.beans.FiltroAcertoEntrada

class TabAcertoEstoqueEntradaViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoEstoqueEntrada

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = AcertoEntrada.findAll(filtro)
    subView.updateClientes(notas)
  }
}

interface ITabAcertoEstoqueEntrada : ITabView {
  fun filtro(): FiltroAcertoEntrada
  fun updateClientes(notas: List<AcertoEntradaNota>)
}
