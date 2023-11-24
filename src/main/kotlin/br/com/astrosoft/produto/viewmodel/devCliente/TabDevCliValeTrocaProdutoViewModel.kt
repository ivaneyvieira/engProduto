package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.ValeTrocaDevolucao

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

  fun imprimeValeTroca(nota: EntradaDevCli) {
    val relatorio = ValeTrocaDevolucao(nota)
    relatorio.print(nota.produtos(), subView.printerPreview {
      nota.marcaImpresso("S")
      updateView()
    })
  }

  val subView
    get() = viewModel.view.tabDevCliValeTrocaProduto
}

interface ITabDevCliValeTrocaProduto : ITabView {
  fun filtro(): FiltroEntradaDevCliProList
  fun updateNotas(produtos: List<EntradaDevCliProList>)
}