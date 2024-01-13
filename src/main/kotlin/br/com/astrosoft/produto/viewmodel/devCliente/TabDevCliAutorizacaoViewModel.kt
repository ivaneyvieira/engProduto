package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaAutorizacao
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaAutorizacao

class TabDevCliAutorizacaoViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaAutorizacao.findAll(filtro)
    subView.updateNotas(notas)
  }

  val subView
    get() = viewModel.view.tabDevCliAutorizacao
}

interface ITabDevCliAutorizacao : ITabView {
  fun filtro(): FiltroNotaAutorizacao
  fun updateNotas(notas: List<NotaAutorizacao>)
}