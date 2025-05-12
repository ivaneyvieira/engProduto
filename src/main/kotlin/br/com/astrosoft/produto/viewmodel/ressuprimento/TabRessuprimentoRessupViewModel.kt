package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.DadosProdutosRessuprimento
import br.com.astrosoft.produto.model.beans.DadosRessuprimento
import br.com.astrosoft.produto.model.beans.EMarcaRessuprimento
import br.com.astrosoft.produto.model.beans.FiltroDadosProdutosRessuprimento
import br.com.astrosoft.produto.model.beans.FiltroRessuprimento
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.model.beans.Ressuprimento

class TabRessuprimentoRessupViewModel(val viewModel: RessuprimentoViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val ressuprimento = DadosRessuprimento.find(filtro)
    subView.updateRessuprimentos(ressuprimento)
  }

  val subView
    get() = viewModel.view.tabRessuprimentoRessup
}

interface ITabRessuprimentoRessup : ITabView {
  fun filtro(): FiltroDadosProdutosRessuprimento
  fun updateRessuprimentos(ressuprimentos: List<DadosRessuprimento>)
  fun updateProdutos()
  fun produtosSelecionados(): List<DadosProdutosRessuprimento>
  fun updateProduto(produto: DadosProdutosRessuprimento)
}