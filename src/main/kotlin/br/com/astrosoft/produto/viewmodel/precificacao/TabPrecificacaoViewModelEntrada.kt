package br.com.astrosoft.produto.viewmodel.precificacao

import br.com.astrosoft.produto.model.beans.Loja

class TabPrecificacaoEntradaViewModel(viewModel: PrecificacaoViewModel) : TabPrecificacaoAbstractViewModel(viewModel) {
  override val subView
    get() = viewModel.view.tabPrecificacaoEntradaViewModel

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas() + Loja(
      no = 10,
      sname = "ADM",
      name = "ADM"
    )
  }
}

