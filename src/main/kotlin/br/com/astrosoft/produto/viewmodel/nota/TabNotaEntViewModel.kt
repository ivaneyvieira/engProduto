package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabNotaEntViewModel(val viewModel: NotaViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaNota.ENT)
    val notas = NotaSaida.find(filtro)
    subView.updateNotas(notas)
  }

  fun marcaCD() {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach{produtoNF ->
      produtoNF.marca = EMarcaNota.CD.num
      produtoNF.usuarioCD = ""
      produtoNF.salva()
    }
    subView.updateProdutos()
    updateView()
  }

  fun printEtiqueta(chave: String?) = viewModel.exec {
    chave ?: fail("Chave não encontrada")
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.print(impressora, chave)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  val subView
    get() = viewModel.view.tabNotaEnt
}

interface ITabNotaEnt : ITabView {
  fun filtro(marca : EMarcaNota) : FiltroNota
  fun updateNotas(notas : List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNF>
}