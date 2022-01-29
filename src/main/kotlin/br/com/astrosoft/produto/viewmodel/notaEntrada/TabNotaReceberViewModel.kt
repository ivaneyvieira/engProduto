package br.com.astrosoft.produto.viewmodel.notaEntrada

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.ProdutoNFE
import okhttp3.internal.notifyAll

class TabNotaEntradaReceberViewModel(val viewModel: NotaEntradaViewModel) {
  val subView
    get() = viewModel.view.tabNotaEntradaReceber

  fun updateView() {
    val lista = NotaEntrada.findNotaEntradaReceber()
    subView.updateNotas(lista)
  }

  fun adicionaChave(chave: String?) {
    if (!chave.isNullOrBlank()) {
      val nota = NotaEntrada.marcaNotaEntradaReceber(chave)
      if (nota == null) {
        viewModel.showError("Nota não encontrada")
      }
      updateView()
    }
  }

  fun marcaProdutos(barCode: String?, quant: Int?) {
    val nota = subView.notaSelecionada()
    nota?.addProdutoReceber(barCode ?: "", quant ?: 0)
    subView.updateViewProduto()
  }

  fun produtos(): List<ProdutoNFE> {
    return subView.notaSelecionada()?.produtosReceber().orEmpty()
  }

  fun removeNota(nota: NotaEntrada?) = viewModel.exec{
    nota ?: fail("Nota não selecionada")
    nota.removeReceber()
    updateView()
  }

  fun removeProduto(produto: ProdutoNFE?) {
    produto ?: fail("Produto não selecionado")
    produto.revomeProdutoReceber()
    subView.updateViewProduto()
  }
}

interface ITabNotaEntradaReceber : ITabView {
  fun updateNotas(notas: List<NotaEntrada>)
  fun notaSelecionada(): NotaEntrada?
  fun updateViewProduto()
}