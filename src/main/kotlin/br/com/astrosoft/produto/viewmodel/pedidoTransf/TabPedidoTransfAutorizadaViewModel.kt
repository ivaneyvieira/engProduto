package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrinterCups
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.RequisicaoTransferencia
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabPedidoTransfAutorizadaViewModel(val viewModel: PedidoTransfViewModel) {
  fun updateView() {
    val filtro = subView.filtro()
    val pedidos = PedidoTransf.findTransf(filtro, false)
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
        EtiquetaChave.printPreviewEnt(setOf(impressora), produto, 1)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  fun imprimePedido(pedido: PedidoTransf, impressora: String, loja: Int) = viewModel.exec {
    viewModel.view.showQuestion("Impressão do pedido na impressora $impressora") {
      val relatorio = RequisicaoTransferencia(pedido)
      relatorio.print(dados = pedido.produtos(), printer = PrinterCups(impressora, loja))
    }
  }

  fun autorizaPedido(pedido: PedidoTransf, login: String, senha: String) = viewModel.exec {
    val user = UserSaci.findAll()
      .firstOrNull { it.login.equals(login, ignoreCase = true) && it.senha.equals(senha, ignoreCase = true) }
    user ?: fail("Usuário ou senha inválidos")

    pedido.autoriza(user)
    updateView()
  }

  fun previewPedido(pedido: PedidoTransf) {
    val relatorio = RequisicaoTransferencia(pedido)
    val rota = pedido.rotaPedido()
    relatorio.print(
      dados = pedido.produtos(),
      printer = subView.printerPreview(rota = rota, loja = pedido.lojaNoDes ?: 0)
    )
  }

  fun mudaParaReservado(pedido: PedidoTransf) = viewModel.exec {
    val situacao = pedido.situacao ?: fail("Pedido sem situação")
    if (situacao != 1) fail("Pedido não está orçado")

    viewModel.view.showQuestion("Confirma a mudança para reservado?") {
      val user = AppConfig.userLogin() as? UserSaci ?: fail("Usuário não encontrado")
      pedido.mudaParaReservado(user.no)
      updateView()
    }
  }

  fun allPrinters(): List<String> {
    val impressoras = Impressora.allTermica().map { it.name }
    return impressoras.distinct().sorted() + (ETipoRota.entries - ETipoRota.TODAS).map { it.name }.sorted()
  }

  val subView
    get() = viewModel.view.tabPedidoTransfAutorizada
}

interface ITabPedidoTransfAutorizada : ITabView {
  fun filtro(): FiltroPedidoTransf
  fun updatePedidos(pedidos: List<PedidoTransf>)
}