package br.com.astrosoft.produto.viewmodel.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.DummyPrinter
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.exec
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.ETipoPedido
import br.com.astrosoft.produto.model.beans.FiltroPedido
import br.com.astrosoft.produto.model.beans.Pedido
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.printText.RomaneioSeparacao
import java.time.LocalDateTime

class PedidoRetiraImpressoViewModel(val viewModel: PedidoRetiraViewModel) {
  private val subView
    get() = viewModel.view.tabRetiraImpresso

  private fun listPedidosEntregaImpressoSemNota(): List<Pedido> {
    val numPedido = subView.pedidoImpressoa
    return Pedido
      .listaPedidoImpressoSemNota(
        FiltroPedido(
          tipo = ETipoPedido.RETIRA,
          ecommerce = false,
          dataInicial = null,
          dataFinal = null
        )
      )
      .filter { pedido ->
        pedido.pedido == numPedido || numPedido == 0
      }
  }

  fun updateGridImpresso() {
    subView.updateGrid(listPedidosEntregaImpressoSemNota())
  }

  fun desmarcaSemNota() = exec(viewModel.view) {
    val pedidos = subView.itensSelecionados().ifEmpty { fail("Não há pedido selecionado") }
    pedidos.forEach { pedido ->
      pedido.desmarcaImpresso()
    }

    updateGridImpresso()
  }

  fun confirmaPrint(pedido: Pedido) = viewModel.exec {
    val relatorio = RomaneioSeparacao()
    val dummyPrinter = DummyPrinter()

    relatorio.print(dados = pedido.produtos(), printer = dummyPrinter)

    val userSaci = AppConfig.userLogin() as? UserSaci
    val impressora = userSaci?.impressoraRet ?: ""

    viewModel.view.showPrintText(dummyPrinter.text(), printerUser = listOf(impressora)) {
      if (pedido.dataHoraPrint == null) {
        pedido.marcaImpresso()
        pedido.marcaDataHora(LocalDateTime.now())
      }
      updateGridImpresso()
    }
  }
}

interface IPedidoRetiraImpresso : ITabView {
  fun updateGrid(itens: List<Pedido>)
  fun itensSelecionados(): List<Pedido>
  val pedidoImpressoa: Int
}