package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrinterCups
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.RequisicaoTransferencia
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabPedidoTransfImprimirViewModel(val viewModel: PedidoTransfViewModel) {
  fun updateView() {
    val filtro = subView.filtro()
    val pedidos = PedidoTransf.findTransf(filtro)
    subView.updatePedidos(pedidos)
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  private fun imprimeEtiquetaEnt(produto: List<ProdutoPedidoTransf>) {
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, produto)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  fun imprimePedido(pedido: PedidoTransf, impressora: String) = viewModel.exec {
    viewModel.view.showQuestion("Impressão do pedido na impressora $impressora") {
      val relatorio = RequisicaoTransferencia(pedido)
      relatorio.print(dados = pedido.produtos(), printer = PrinterCups(impressora))
    }
  }

  fun autorizaPedido(pedido: PedidoTransf, login: String, senha: String) = viewModel.exec {
    val user = UserSaci.findAll()
      .firstOrNull { it.login.uppercase() == login.uppercase() && it.senha.uppercase() == senha.uppercase() }
    user ?: fail("Usuário ou senha inválidos")

    pedido.autoriza(user)
    updateView()
  }

  fun previewPedido(pedido: PedidoTransf, printEvent: (impressora: String) -> Unit) {
    val relatorio = RequisicaoTransferencia(pedido)
    val printerRota = pedido.printerRota()
    relatorio.print(
      dados = pedido.produtos(),
      printer = subView.printerPreview(printerRota = printerRota, printEvent = printEvent)
    )
  }

  fun marcaImpressao(pedido: PedidoTransf, impressora: String) = viewModel.exec {
    val printer = Impressora.findImpressora(impressora) ?: fail("Impressora não encontrada")
    pedido.marca(printer)
    updateView()
  }

  val subView
    get() = viewModel.view.tabPedidoTransfImprimir
}

interface ITabPedidoTransfImprimir : ITabView {
  fun filtro(): FiltroPedidoTransf
  fun updatePedidos(pedidos: List<PedidoTransf>)
}