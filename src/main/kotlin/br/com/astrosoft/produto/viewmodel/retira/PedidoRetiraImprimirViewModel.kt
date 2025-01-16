package br.com.astrosoft.produto.viewmodel.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.DummyPrinter
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.RomaneioSeparacao
import br.com.astrosoft.produto.model.printText.RomaneioSeparacaoL
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
    val relatorio = if (pedido.tipoRetiraEnum == ETipoRetira.RETIRA_FUTURA_L) {
      RomaneioSeparacaoL()
    } else {
      RomaneioSeparacao()
    }
    val dummyPrinter = DummyPrinter()

    relatorio.print(dados = pedido.produtos(), printer = dummyPrinter)

    val userSaci = AppConfig.userLogin() as? UserSaci
    val impressora = userSaci?.impressoraRet.orEmpty()

    viewModel.view.showPrintText(
      dummyPrinter.textBuffer(),
      loja = 0,
      printerUser = impressora.toList()
    ) {
      if (pedido.dataHoraPrint == null) {
        pedido.marcaImpresso()
        pedido.marcaDataHora(LocalDateTime.now())
      }
      updateGridImprimir()
    }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }
}

interface IPedidoRetiraImprimir : ITabView {
  fun updateGrid(itens: List<Pedido>)
  fun itensSelecionados(): List<Pedido>
  fun filtro(): FiltroPedido
}