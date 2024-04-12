package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.model.printText.PrinterCups
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaRessu4
import br.com.astrosoft.produto.model.printText.NotaTransferencia
import br.com.astrosoft.produto.model.report.ReportRessu4

class TabPedidoTransfRessu4ViewModel(val viewModel: PedidoTransfViewModel) {
  fun updateView() {
    val filtro = subView.filtro()
    val pedidos = TransfRessu4.findAll(filtro)
    subView.updatePedidos(pedidos)
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun geraPlanilha(pedido: TransfRessu4): ByteArray {
    val planilha = PlanilhaRessu4()
    return planilha.write(pedido.produtos())
  }

  fun imprimeRelatorio(pedido: TransfRessu4) {
    val report = ReportRessu4()
    val file = report.processaRelatorio(pedido.produtos())
    viewModel.view.showReport(chave = "Ressuprimento4${System.nanoTime()}", report = file)
  }

  fun imprimeNota(nota: TransfRessu4, impressora: String, loja: Int) {
    viewModel.view.showQuestion("Impressão do pedido na impressora $impressora") {
      val relatorio = NotaTransferencia()
      relatorio.print(dados = nota.produtos(), printer = PrinterCups(impressora, loja))
    }
  }

  fun previewNota(nota: TransfRessu4) {
    val relatorio = NotaTransferencia()
    relatorio.print(
      dados = nota.produtos(),
      printer = subView.printerPreview(loja = nota.lojaDestinoNo ?: 0, printEvent = {})
    )
  }

  fun allPrinters(): List<String> {
    val impressoras = Impressora.allTermica().map { it.name }
    return impressoras.distinct().sorted() + (ETipoRota.entries - ETipoRota.TODAS).map { it.name }.sorted()
  }

  val subView
    get() = viewModel.view.tabPedidoTransfRessu4
}

interface ITabPedidoTransfRessu4 : ITabView {
  fun filtro(): FiltroPedidoRessu4
  fun updatePedidos(pedidos: List<TransfRessu4>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoTransfRessu4>
}