package br.com.astrosoft.produto.viewmodel.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.DummyPrinter
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroPedido
import br.com.astrosoft.produto.model.beans.Pedido
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.printText.RomaneioSeparacao
import java.time.LocalDateTime

class PedidoRetiraImprimirViewModel(val viewModel: PedidoRetiraViewModel) {
  private val subView
    get() = viewModel.view.tabRetiraImprimir

  private fun listPedidosEntregaImprimir(): List<Pedido> {
    val filtro = subView.filtro()
    return Pedido.listaPedidoImprimir(filtro)
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

    viewModel.view.showPrintText(
      dummyPrinter.text(),
      printerUser = listOf(impressora)
    ) {
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
  fun filtro(): FiltroPedido
}