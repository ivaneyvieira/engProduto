package br.com.astrosoft.produto.viewmodel.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.DummyPrinter
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.ETipoPedido
import br.com.astrosoft.produto.model.beans.FiltroPedido
import br.com.astrosoft.produto.model.beans.Pedido
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.printText.RomaneioSeparacao
import java.time.LocalDate
import java.time.LocalDateTime

class PedidoRetiraImprimirViewModel(val viewModel: PedidoRetiraViewModel) {
  private val subView
    get() = viewModel.view.tabRetiraImprimir

  private fun listPedidosEntregaImprimir(): List<Pedido> {
    val numPedido = subView.pedidoImprimir
    val data = subView.dataImprimir
    val area = subView.areaImprimir.trim()
    val rota = subView.rotaImprimir.trim()
    return Pedido
      .listaPedidoImprimir(
        FiltroPedido(
          tipo = ETipoPedido.RETIRA,
          ecommerce = false,
          dataInicial = null,
          dataFinal = null
        )
      )
      .filter { pedido ->
        (pedido.pedido == numPedido || numPedido == 0) &&
        (pedido.data == data || data == null) && (pedido.rota?.contains(rota) == true || rota == "") &&
        (pedido.area?.contains(area) == true || area == "")
      }
  }

  fun updateGridImprimir() {
    subView.updateGrid(listPedidosEntregaImprimir())
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
      updateGridImprimir()
    }
  }
}

interface IPedidoRetiraImprimir : ITabView {
  fun updateGrid(itens: List<Pedido>)
  fun itensSelecionados(): List<Pedido>
  val pedidoImprimir: Int
  val dataImprimir: LocalDate?
  val areaImprimir: String
  val rotaImprimir: String
}