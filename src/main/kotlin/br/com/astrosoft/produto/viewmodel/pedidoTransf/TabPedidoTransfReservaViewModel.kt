package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrinterCups
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.RequisicaoTransferencia
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabPedidoTransfReservaViewModel(val viewModel: PedidoTransfViewModel) {
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
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    if (!user.admin) {
      val lojaUserSaci = user.lojaUsuario
      val lojaDestinoPedido = pedido.lojaNoDes ?: fail("Loja destino não encontrada")
      if (lojaUserSaci != lojaDestinoPedido) fail("Usuário não autorizado para esta loja")
    }

    pedido.autoriza(user)

    updateView()
  }

  private fun List<String>.listaCampos(): String {
    return when {
      this.isEmpty() -> ""
      this.size == 1 -> this.first()
      this.size == 2 -> this.first() + " e " + this.last()
      else           -> this.dropLast(1).joinToString(", ") + " e " + this.last()
    }
  }

  fun formAutoriza(pedido: PedidoTransf) = viewModel.exec {
    val camposVazios = listOfNotNull(
      if (pedido.referente.isNullOrEmpty()) "Referência"
      else null,
      if (pedido.recebido.isNullOrEmpty()) "Recebido"
      else null,
      if (pedido.entregue.isNullOrEmpty()) "Entregue"
      else null,
    )
    if (camposVazios.isNotEmpty()) {
      val campos = camposVazios.listaCampos()
      if (camposVazios.size == 1)
        fail("O campo $campos está vazio")
      else
        fail("Os campos $campos estão vazios")
    }
    subView.formAutoriza(pedido)
  }

  fun previewPedido(pedido: PedidoTransf, printEvent: (impressora: String) -> Unit) = viewModel.exec {
    val relatorio = RequisicaoTransferencia(pedido)
    val rota = pedido.rotaPedido()
    if (pedido.nameSing.isNullOrEmpty())
      fail("Imprimir após Autorização")
    relatorio.print(
      dados = pedido.produtos(),
      printer = subView.printerPreview(rota = rota, printEvent = printEvent)
    )
  }

  fun marcaImpressao(pedido: PedidoTransf, impressora: String) = viewModel.exec {
    val printer = Impressora.findImpressora(impressora) ?: fail("Impressora não encontrada")
    pedido.marca(printer)
    updateView()
  }

  val subView
    get() = viewModel.view.tabPedidoTransfReserva
}

interface ITabPedidoTransfReserva : ITabView {
  fun filtro(): FiltroPedidoTransf
  fun updatePedidos(pedidos: List<PedidoTransf>)
  fun formAutoriza(pedido: PedidoTransf)
}