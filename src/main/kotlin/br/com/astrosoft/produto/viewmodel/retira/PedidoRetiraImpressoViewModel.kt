package br.com.astrosoft.produto.viewmodel.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.DummyPrinter
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.RomaneioSeparacao
import br.com.astrosoft.produto.model.printText.RomaneioSeparacaoL
import java.time.LocalDateTime

class PedidoRetiraImpressoViewModel(val viewModel: PedidoRetiraViewModel) {
  private val subView
    get() = viewModel.view.tabRetiraImpresso

  private fun listPedidosEntregaImpressoSemNota(): List<Pedido> {
    return Pedido.listaPedidoImpressoSemNota(subView.filtro())
  }

  fun updateGridImpresso() {
    subView.updateGrid(listPedidosEntregaImpressoSemNota())
  }

  fun desmarcaSemNota() = viewModel.exec {
    val pedidos = subView.itensSelecionados().ifEmpty { fail("Não há pedido selecionado") }
    pedidos.forEach { pedido ->
      pedido.desmarcaImpresso()
    }

    updateGridImpresso()
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

    val text = dummyPrinter.textBuffer()

    viewModel.view.showPrintText(
      text,
      showPrinter = userSaci?.admin == true,
      loja = 0,
      printerUser = impressora.toList()
    ) {
      if (pedido.dataHoraPrint == null) {
        pedido.marcaImpresso()
        pedido.marcaDataHora(LocalDateTime.now())
      }
      updateGridImpresso()
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

interface IPedidoRetiraImpresso : ITabView {
  fun updateGrid(itens: List<Pedido>)
  fun itensSelecionados(): List<Pedido>
  fun filtro(): FiltroPedido
}