package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EntradaDevCliProList
import br.com.astrosoft.produto.model.beans.FiltroEntradaDevCliProList
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.printText.ProdutosDevolucao

class TabDevCliProdutoViewModel(val viewModel: DevClienteViewModel) {
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

  val subView
    get() = viewModel.view.tabDevCliProduto
}

interface ITabDevCliProduto : ITabView {
  fun filtro(): FiltroEntradaDevCliProList
  fun updateProdutos(produtos: List<EntradaDevCliProList>)
  fun produtosSelecionados(): List<EntradaDevCliProList>
  fun reloadGrid()
}