package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.ProdutosDevolucao

class TabEstoqueDevProdutoViewModel(val viewModel: EstoqueCDViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val produtos = EntradaDevCliProList.findAll(filtro).filter {
      it.tipoNotaPre.endsWith(" P")
    }

    subView.updateProdutos(produtos)
  }

  fun imprimeProdutos() = viewModel.exec {
    val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Não há produtos selecionados")
    }

    if (produtos.any { it.userEntregaNo == 0 || it.userEntregaNo == null }) {
      fail("Possui produtos com entrega não autorizada")
    }

    if (produtos.any { it.userRecebimentoNo == 0 || it.userRecebimentoNo == null }) {
      fail("Possui produtos com recebimento não autorizada")
    }

    val countEntregador = produtos.map { it.userEntregaNo ?: 0 }.distinct().size
    if (countEntregador != 1) {
      fail("Possui mais de um entregador")
    }

    val countRecebedor = produtos.map { it.userRecebimentoNo ?: 0 }.distinct().size
    if (countRecebedor != 1) {
      fail("Possui mais de um recebedor")
    }

    val countTipo = produtos.map { it.produtoTipoP }.distinct().size
    if (countTipo != 1) {
      fail("Foi seleciona produtos de mais de um tipo")
    }

    val relatorio = ProdutosDevolucao("Devolucoes de Clientes com Produtos")
    relatorio.print(produtos.sortedBy { it.ni }, subView.printerPreview(loja = 0))
  }

  fun updateKardex() = viewModel.exec {
    val produtos = subView.produtosSelecionados()
      .flatMap {
        ProdutoEstoque.findProdutoEstoque(loja = it.codLoja, prdno = it.prdno, grade = it.grade)
      }
    ProcessamentoKardec.updateKardex(produtos, ETipoKardec.DEVOLUCAO)
    subView.reloadGrid()
  }

  val subView
    get() = viewModel.view.tabEstoqueDevProduto
}

interface ITabEstoqueDevProduto : ITabView {
  fun filtro(): FiltroEntradaDevCliProList
  fun updateProdutos(produtos: List<EntradaDevCliProList>)
  fun produtosSelecionados(): List<EntradaDevCliProList>
  fun reloadGrid()
}