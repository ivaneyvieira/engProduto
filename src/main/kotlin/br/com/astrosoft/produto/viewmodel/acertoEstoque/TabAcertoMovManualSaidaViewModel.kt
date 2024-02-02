package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*

class TabAcertoMovManualSaidaViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoMovManualSaida

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = MovManual.findMovManual(filtro)
    subView.updateNotas(notas)
  }
}

interface ITabAcertoMovManualSaida : ITabView {
  fun filtro(): MovManualFilter
  fun updateNotas(nota: List<MovManual>)
}