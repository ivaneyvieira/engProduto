package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.Validade

class TabValidadeListViewModel(val viewModel: EstoqueCDViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val validadeList = Validade.findAll()
    subView.updateValidades(validadeList)
  }

  fun addValidade() {
    val list = Validade.findAll()
    subView.updateValidades(list + Validade(0, 0))
  }

  fun deleteValidade(bean: Validade) {
    viewModel.view.showQuestion("Deseja excluir a validade ${bean.validade}?") {
      bean.delete()
      updateView()
    }
  }

  fun salvaValidade(bean: Validade?) {
    bean?.salve()
    updateView()
  }

  val subView
    get() = viewModel.view.tabValidadeList
}

interface ITabValidadeList : ITabView {
  fun updateValidades(validadeList: List<Validade>)
}
