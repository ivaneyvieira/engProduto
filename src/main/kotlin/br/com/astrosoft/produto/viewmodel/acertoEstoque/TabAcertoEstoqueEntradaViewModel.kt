package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.AcertoEntrada
import br.com.astrosoft.produto.model.beans.AcertoEntradaNota
import br.com.astrosoft.produto.model.beans.FiltroAcertoEntrada
import br.com.astrosoft.produto.model.beans.Loja

class TabAcertoEstoqueEntradaViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoEstoqueEntrada

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = AcertoEntrada.findAll(filtro)
    subView.updateNotas(notas)
  }
}

interface ITabAcertoEstoqueEntrada : ITabView {
  fun filtro(): FiltroAcertoEntrada
  fun updateNotas(notas: List<AcertoEntradaNota>)
}
