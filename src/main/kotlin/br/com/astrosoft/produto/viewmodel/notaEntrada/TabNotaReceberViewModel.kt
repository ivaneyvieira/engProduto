package br.com.astrosoft.produto.viewmodel.notaEntrada

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaEntrada
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.ProdutoNFE

class TabNotaEntradaReceberViewModel(val viewModel: NotaEntradaViewModel) {
  val subView
    get() = viewModel.view.tabNotaEntradaReceber

  fun updateView() {
    val lista = NotaEntrada.findNotaEntradaReceber(FiltroNotaEntrada())
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
}

interface ITabNotaEntradaReceber : ITabView {
  fun updateNotas(notas: List<NotaEntrada>)
  fun notaSelecionada(): NotaEntrada?
  fun updateViewProduto()
}