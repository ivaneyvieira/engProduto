package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.DevTroca
import br.com.astrosoft.produto.model.beans.FiltroDevTroca
import br.com.astrosoft.produto.model.beans.Loja

class TabDevCliDevTrocaViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = DevTroca.findAll(filtro)
    subView.updateNotas(notas)
  }

  val subView
    get() = viewModel.view.tabDevCliDevTroca
}

interface ITabDevCliDevTroca : ITabView {
  fun filtro(): FiltroDevTroca
  fun updateNotas(notas: List<DevTroca>)
}