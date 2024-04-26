package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaRecebimentoProduto
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaRecebimento

class TabRecebidoViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabRecebido

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaRecebimento.findAll(filtro)
    subView.updateNota(notas)
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }
}

interface ITabRecebido : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
}