package br.com.astrosoft.produto.viewmodel.notaEntrada

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.ProdutoNFE

class TabNotaEntradaReceberViewModel(val viewModel: NotaEntradaViewModel) {
  val subView
    get() = viewModel.view.tabNotaEntradaReceber

  fun findNotaEntradaReceber() = NotaEntrada.findNotaEntradaReceber()

  fun updateView() {
    val lista = findNotaEntradaReceber()
    subView.updateNotas(lista)
  }

  fun adicionaChave(chave: String?) {
    if (!chave.isNullOrBlank()) {
      val nota = NotaEntrada.marcaNotaEntradaReceber(chave)
      if (nota == null) {
        viewModel.showError("Nota não encontrada")
        updateView()
      }
    }
  }

  fun marcaProdutos(barCode: String?, quant: Int?) {
    val nota = subView.notaSelecionada()
    nota?.addProdutoReceber(barCode ?: "", quant ?: 0)
    subView.updateViewProduto()
  }

  fun produtos(): List<ProdutoNFE> {
    return produtosTotal().filter { it.marca >= 0 }
  }

  private fun produtosTotal(): List<ProdutoNFE> {
    return subView.notaSelecionada()?.produtosReceber().orEmpty()
  }

  fun removeNota(nota: NotaEntrada?) = viewModel.exec {
    nota ?: fail("Nota não selecionada")

    viewModel.showQuestion("Remover nota ${nota.numero}/${nota.serie}?", execYes = {
      nota.removeReceber()
      updateView()
    })
  }

  fun removeProduto() = viewModel.exec {
    val produtos = subView.produtosSelecionados().ifEmpty {
      fail("Não há nenhum produto selecionado")
    }
    viewModel.showQuestion("Remover produtos selecionados?", execYes = {
      produtos.forEach { produto ->
        produto.revomeProdutoReceber()
      }
      subView.updateViewProduto()
    })
  }

  fun processaProdutos() = viewModel.exec {
    val produtosTotal = produtosTotal()

    if(produtosTotal.any { it.marca == -1 }){
      fail("Está faltando produto da nota fiscal")
    }

    if(produtosTotal.any { it.qttyRef == null }){
      fail("Existe produto sobrando")
    }

    if(produtosTotal.any { it.qttyRef != it.quantidade }){
      fail("Ainda tem quantidades divergentes")
    }

    val produtos = subView.produtosNota().ifEmpty {
      fail("Não há nenhum produto selecionado")
    }
    produtos.forEach { produto ->
      produto.processaReceber()
    }
    subView.updateViewProduto()
  }

  fun saveProduto(produto: ProdutoNFE?) {
    produto ?: fail("Não há produto selecionado")
    produto.saveProdutoReceber()
    subView.updateViewProduto()
  }
}

interface ITabNotaEntradaReceber : ITabView {
  fun updateNotas(notas: List<NotaEntrada>)
  fun notaSelecionada(): NotaEntrada?
  fun updateViewProduto()
  fun produtosNota(): List<ProdutoNFE>
  fun produtosSelecionados(): List<ProdutoNFE>
}