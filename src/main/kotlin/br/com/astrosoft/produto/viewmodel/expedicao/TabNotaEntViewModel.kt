package br.com.astrosoft.produto.viewmodel.expedicao

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
    val filtro = subView.filtro()
    val notas = NotaSaida.find(filtro).filter {
      it.cancelada != "S"
    }
    subView.updateNotas(notas)
  }

  fun marcaCD() {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaNota.CD.num
      produtoNF.usernoCD = 0
      produtoNF.salva()
    }
    subView.updateProdutos()
  }

  fun printEtiquetaEnt(nota: NotaSaida?) = viewModel.exec {
    nota ?: fail("Nenhuma expedicao selecionada")
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressoraNota?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, nota.produtos(EMarcaNota.ENT, todosLocais = false), copias = 2)
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
  fun filtro(): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNFS>
}