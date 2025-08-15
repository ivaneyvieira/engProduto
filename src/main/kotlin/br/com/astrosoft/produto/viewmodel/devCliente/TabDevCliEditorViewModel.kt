package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.ValeTrocaDevolucao

class TabDevCliEditorViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = EntradaDevCli.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun imprimeValeTroca(nota: EntradaDevCli) {
    if (!nota.isAjusteMisto()) {
      subView.ajustaProduto(nota)
    }

    if (nota.isAjusteMisto()) {
      val relatorio = ValeTrocaDevolucao(nota)
      relatorio.print(nota.produtos(), subView.printerPreview(showPrinter = AppConfig.isAdmin, loja = 0) { impressora ->
        nota.marcaImpresso(Impressora(0, impressora))
        updateView()
      })
    }
  }

  fun ajusteProduto(ajuste: AjusteProduto) {
    val produto = ajuste.produto
    produto.marcaAjuste(ajuste)
  }

  val subView
    get() = viewModel.view.tabDevCliEditor
}

interface ITabDevCliEditor : ITabView {
  fun filtro(): FiltroEntradaDevCli
  fun updateNotas(notas: List<EntradaDevCli>)
  fun ajustaProduto(nota: EntradaDevCli)
}