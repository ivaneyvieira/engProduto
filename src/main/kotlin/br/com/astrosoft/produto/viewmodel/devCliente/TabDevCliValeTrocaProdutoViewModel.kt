package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EntradaDevCliProList
import br.com.astrosoft.produto.model.beans.FiltroEntradaDevCliProList
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.printText.ProdutosDevolucao

class TabDevCliValeTrocaProdutoViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val produtos = EntradaDevCliProList.findAll(filtro)
    subView.updateNotas(produtos)
  }

  fun imprimeProdutos() = viewModel.exec {
    val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Não há produtos selecionados")
    }
    val relatorio = ProdutosDevolucao()
    relatorio.print(produtos, subView.printerPreview())
  }

  val subView
    get() = viewModel.view.tabDevCliValeTrocaProduto
}

interface ITabDevCliValeTrocaProduto : ITabView {
  fun filtro(): FiltroEntradaDevCliProList
  fun updateNotas(produtos: List<EntradaDevCliProList>)

  fun produtosSelecionados(): List<EntradaDevCliProList>
}