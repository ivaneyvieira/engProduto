package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import br.com.astrosoft.produto.model.beans.FiltroEntradaDevCli
import br.com.astrosoft.produto.model.beans.Loja

class TabDevCancelaViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = EntradaDevCli.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun desfazTroca() = viewModel.exec {
    val itens = subView.notasSelecionados()
    if (itens.isEmpty()) {
      fail("Nenhuma nota selecionada")
    }

    viewModel.view.showQuestion("Confirma desfazer cancelamento?") {
      itens.forEach { nota ->
        nota.desfazTroca()
      }

      updateView()
    }
  }

  val subView
    get() = viewModel.view.tabDevCancela
}

interface ITabDevCancela : ITabView {
  fun filtro(): FiltroEntradaDevCli
  fun updateNotas(notas: List<EntradaDevCli>)
  fun notasSelecionados(): List<EntradaDevCli>
}