package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.DadosDevProduto
import br.com.astrosoft.produto.model.beans.FiltroDadosDev
import br.com.astrosoft.produto.model.beans.Loja

class TabDevDadosProdutoViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val produtos = DadosDevProduto.findAll(filtro)
    subView.updateProdutos(produtos)
  }

  fun imprimeProdutos() = viewModel.exec {
    /*val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Não há produtos selecionados")
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
    relatorio.print(produtos.sortedBy { it.ni }, subView.printerPreview(loja = 0))*/
  }

  val subView
    get() = viewModel.view.tabDevDadosProduto
}

interface ITabDevDadosProduto : ITabView {
  fun filtro(): FiltroDadosDev
  fun updateProdutos(produtos: List<DadosDevProduto>)
  fun produtosSelecionados(): List<DadosDevProduto>
  fun reloadGrid()
}