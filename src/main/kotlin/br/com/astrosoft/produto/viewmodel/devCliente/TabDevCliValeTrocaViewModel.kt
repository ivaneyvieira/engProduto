package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.beans.PedidoTransf

class TabDevCliValeTrocaViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = EntradaDevCli.findAll(filtro)
    subView.updateNotas(notas)
  }

  val subView
    get() = viewModel.view.tabDevCliValeTroca
}

interface ITabDevCliValeTroca : ITabView {
  fun filtro(): FiltroEntradaDevCli
  fun updateNotas(notas: List<EntradaDevCli>)
}