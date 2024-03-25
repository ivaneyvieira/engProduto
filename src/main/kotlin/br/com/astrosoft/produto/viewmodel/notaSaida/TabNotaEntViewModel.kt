package br.com.astrosoft.produto.viewmodel.notaSaida

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabNotaEntViewModel(val viewModel: NotaViewModel) {
  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

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
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaNota.CD.num
      produtoNF.usuarioCD = ""
      produtoNF.salva()
    }
    subView.updateProdutos()
  }

  fun printEtiquetaEnt(nota: NotaSaida?) = viewModel.exec {
    nota ?: fail("Nenhuma notaSaida selecionada")
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressoraNota?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, nota.produtos(EMarcaNota.ENT))
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
  fun filtro(marca: EMarcaNota): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNFS>
}