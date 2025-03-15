package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroAcerto
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import br.com.astrosoft.produto.model.beans.agrupa
import br.com.astrosoft.produto.model.printText.PrintProdutosConferenciaAcerto

class TabEstoqueAcertoViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabEstoqueAcerto

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoEstoqueAcerto.findAll(filtro).agrupa().sortedBy { it.numero }
    subView.updateProduto(produtos)
  }

  fun imprimir(acerto: ProdutoEstoqueAcerto) = viewModel.exec {
    val produtos =
        ProdutoEstoqueAcerto.findAll(FiltroAcerto(numLoja = acerto.numloja ?: 0, numero = acerto.numero ?: 0)).filter {
          (it.diferenca ?: 0) != 0
        }.sortedBy { it.diferenca ?: 999999 }
    if (produtos.isEmpty()) {
      fail("Nenhum produto válido selecionado")
    }

    val report = PrintProdutosConferenciaAcerto()

    report.print(
      dados = produtos, printer = subView.printerPreview()
    )
  }
}

interface ITabEstoqueAcerto : ITabView {
  fun filtro(): FiltroAcerto
  fun updateProduto(produtos: List<ProdutoEstoqueAcerto>)
}
