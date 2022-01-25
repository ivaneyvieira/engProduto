package br.com.astrosoft.produto.viewmodel.notaEntrada

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*

class TabNotaEntradaReceberViewModel(val viewModel: NotaEntradaViewModel) {
  val subView
    get() = viewModel.view.tabNotaEntradaReceber

  fun updateView() {
    val lista = NotaEntrada.findNotaEntrada(FiltroNotaEntrada())
    subView.updateNotas(lista)
  }

  fun adicionaChave(chave: String?) {
    if(!chave.isNullOrBlank()){
      val nota = NotaEntrada.marcaNotaEntrada(chave)
      if(nota == null){
        viewModel.showError("Nota não encontrada")
      }
      updateView()
    }
  }

  fun marcaProdutos(barCode: String?) {
    TODO("Not yet implemented")
  }
}

interface ITabNotaEntradaReceber : ITabView {
  fun updateNotas(notas: List<NotaEntrada>)
}