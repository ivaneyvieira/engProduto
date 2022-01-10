package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EMarcaNota
import br.com.astrosoft.produto.model.beans.FiltroNota
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.ProdutoNF

class TabNotaCDViewModel(val viewModel: NotaViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaNota.CD)
    val notas = NotaSaida.find(filtro)
    subView.updateNotas(notas)
  }

  fun desmarcaCD() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaNota.EXP.num
      produtoNF.salva()
    }
    subView.updateProdutos()
  }

  fun desmarcaEntProdutod(codigoBarra: String) = viewModel.exec {
    val produtoNF = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produtoNF.marca = EMarcaNota.ENT.num
    produtoNF.salva()
    subView.updateProdutos()
  }

  val subView
    get() = viewModel.view.tabNotaCD
}

interface ITabNotaCD : ITabView {
  fun filtro(marca: EMarcaNota): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNF>
  fun produtosCodigoBarras(codigoBarra: String): ProdutoNF?
}