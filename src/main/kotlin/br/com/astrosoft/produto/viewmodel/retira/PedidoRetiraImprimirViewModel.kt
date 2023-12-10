package br.com.astrosoft.produto.viewmodel.retira

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.exec
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.ETipoPedido
import br.com.astrosoft.produto.model.beans.FiltroPedido
import br.com.astrosoft.produto.model.beans.Pedido
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
        (pedido.pedido == numPedido || numPedido == 0) && (pedido.data == data || data == null) && (pedido.rota.contains(
          rota
        ) || rota == "") && (pedido.area.contains(area) || area == "")
      }
  }

  fun updateGridImprimir() {
    subView.updateGrid(listPedidosEntregaImprimir())
  }

  fun imprimirPedidos(pedidos: List<Pedido>) = exec(viewModel.view) {
    printPedidoPdf(pedidos)
    updateGridImprimir()
  }

  fun confirmaPrint() {
    val pedidos = viewModel.view.tabRetiraImprimir.itensSelecionados().ifEmpty { fail("Não há pedido selecionado") }

    pedidos.forEach { pedido ->
      if (pedido.dataHoraPrint != null) pedido.marcaImpresso()
    }

    updateGridImprimir()
  }

  private fun printPedidoPdf(pedidos: List<Pedido>) {
    viewModel.view.showRelatorioPedido(pedidos)
  }

  fun imprimirPedidoMinuta() = exec(viewModel.view) {
    val datetime = LocalDateTime.now()
    val pedidos = viewModel.view.tabRetiraImprimir.itensSelecionados().ifEmpty { fail("Não há pedido selecionado") }
    printPedidoMinutaPdf(pedidos)
    pedidos.forEach { pedido ->
      pedido.marcaDataHora(datetime)
    }
    updateGridImprimir()
  }

  private fun printPedidoMinutaPdf(pedidos: List<Pedido>) {
    viewModel.view.showRelatorioPedidoMinuta(pedidos)
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