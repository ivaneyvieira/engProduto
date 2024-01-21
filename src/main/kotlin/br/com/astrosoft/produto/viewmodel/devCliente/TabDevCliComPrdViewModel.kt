package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import br.com.astrosoft.produto.model.beans.EntradaDevCliProList
import br.com.astrosoft.produto.model.beans.FiltroEntradaDevCli
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.planilha.PlanilhaImpresso
import br.com.astrosoft.produto.model.printText.ProdutosDevolucao
import br.com.astrosoft.produto.model.printText.ValeTrocaDevolucao
import br.com.astrosoft.produto.model.report.ReportImpresso

class TabDevCliComPrdViewModel(val viewModel: DevClienteViewModel) {
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
    val relatorio = ValeTrocaDevolucao(nota)
    relatorio.print(nota.produtos(), subView.printerPreview(showPrinter = AppConfig.isAdmin, loja = 0) {
      updateView()
    })
  }

  fun imprimeRelatorio() {
    val notas = subView.itensNotasSelecionados()
    val report = ReportImpresso()
    val file = report.processaRelatorio(notas)
    viewModel.view.showReport(chave = "NotaImpresso${System.nanoTime()}", report = file)
  }

  fun geraPlanilha(): ByteArray {
    val notas = subView.itensNotasSelecionados()
    val planilha = PlanilhaImpresso()
    return planilha.write(notas)
  }

  fun imprimeProdutos() {
    val listNi = subView.itensNotasSelecionados().map { it.invno }
    val produtos = EntradaDevCliProList.findAll(listNi)
    if (produtos.isEmpty()) {
      fail("Não há produtos selecionados")
    }
    val relatorio = ProdutosDevolucao("Devolucoes de Clientes")
    relatorio.print(produtos.sortedBy { it.ni }, subView.printerPreview(loja = 0))
  }

  val subView
    get() = viewModel.view.tabDevCliComPrd
}

interface ITabDevCliComPrd : ITabView {
  fun filtro(): FiltroEntradaDevCli
  fun updateNotas(notas: List<EntradaDevCli>)
  fun itensNotasSelecionados(): List<EntradaDevCli>
}