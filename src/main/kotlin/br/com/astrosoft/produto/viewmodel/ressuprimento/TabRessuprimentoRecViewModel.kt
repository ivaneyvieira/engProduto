package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EMarcaRessuprimento
import br.com.astrosoft.produto.model.beans.FiltroRessuprimento
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.model.beans.Ressuprimento
import br.com.astrosoft.produto.model.printText.PrintRessuprimento

class TabRessuprimentoRecViewModel(val viewModel: RessuprimentoViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaRessuprimento.REC)
    val ressuprimento = Ressuprimento.find(filtro)
    subView.updateRessuprimentos(ressuprimento)
  }

  fun previewPedido(pedido: Ressuprimento, printEvent: (impressora: String) -> Unit) = viewModel.exec {
    if (pedido.entreguePor.isNullOrBlank())
      fail("Pedido não autorizado")

    if (pedido.transportadoPor.isNullOrBlank())
      fail("Pedido não transportado")

    val produtos = pedido.produtos()
    val relatorio = PrintRessuprimento(pedido, ProdutoRessuprimento::qtRecebido)

    relatorio.print(
      dados = produtos.sortedWith(
        compareBy(
          ProdutoRessuprimento::descricao,
          ProdutoRessuprimento::codigo,
          ProdutoRessuprimento::grade
        )
      ),
      printer = subView.printerPreview(loja = 1, printEvent = printEvent)
    )
  }

  fun selecionaProdutos(codigoBarra: String) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.selecionado = EMarcaRessuprimento.REC.num
    produto.salva()

    subView.updateProduto(produto)
  }

  fun marcaRec() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter { it.selecionado == EMarcaRessuprimento.REC.num }
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }

    itens.forEach { produto ->
      produto.marca = EMarcaRessuprimento.REC.num
      produto.selecionado = EMarcaRessuprimento.REC.num
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun desmarcar() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter { it.selecionado == EMarcaRessuprimento.REC.num }
    itens.ifEmpty {
      fail("Nenhum produto para desmarcar")
    }

    itens.forEach { produto ->
      produto.selecionado = 0
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun desfazer() = viewModel.exec {
    val selecionados = subView.produtosSelecionados()
    if (selecionados.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    selecionados.forEach { produto ->
      produto.selecionado = 0
      produto.marca = EMarcaRessuprimento.ENT.num
      produto.salva()
    }
    subView.updateProdutos()
  }

  val subView
    get() = viewModel.view.tabRessuprimentoRec
}

interface ITabRessuprimentoRec : ITabView {
  fun filtro(marca: EMarcaRessuprimento): FiltroRessuprimento
  fun updateRessuprimentos(ressuprimentos: List<Ressuprimento>)
  fun updateProdutos()
  fun produtosSelecionados(): List<ProdutoRessuprimento>
  fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento?
  fun updateProduto(produto: ProdutoRessuprimento)
}