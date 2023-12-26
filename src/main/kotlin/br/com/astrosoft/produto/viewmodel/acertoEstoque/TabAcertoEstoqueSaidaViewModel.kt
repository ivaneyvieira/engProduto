package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.AcertoSaida
import br.com.astrosoft.produto.model.beans.AcertoSaidaNota
import br.com.astrosoft.produto.model.beans.FiltroAcertoSaida
import br.com.astrosoft.produto.model.beans.Loja

class TabAcertoEstoqueSaidaViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoEstoqueSaida

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = AcertoSaida.findAll(filtro)
    subView.updateNotas(notas)
  }
}

interface ITabAcertoEstoqueSaida : ITabView {
  fun filtro(): FiltroAcertoSaida
  fun updateNotas(nota: List<AcertoSaidaNota>)
}