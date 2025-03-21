package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.devolucao.model.reports.RelatorioPedido
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EPreEntrada
import br.com.astrosoft.produto.model.beans.FiltroPedidoNota
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.PedidoCapa

class TabPedidoViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabPedido

  fun updateView() {
    val filtro = subView.filtro()
    val pedidos = PedidoCapa.findPedidoCapa(filtro).filter {
      (it.preEntrada == filtro.preEntrada.cod || filtro.preEntrada == EPreEntrada.TODOS) &&
          (!filtro.semRecebimento || it.totalRecebido.format() == "0,00")
    }
    subView.updatePedidos(pedidos)
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun imprimePedido() = viewModel.exec {
    val pedidos = subView.predidoSelecionado()
    pedidos.ifEmpty {
      fail("Nenhum pedido selecionado")
    }
    val file = RelatorioPedido.processaRelatorio(pedidos)
    viewModel.view.showReport(chave = "PedidoImpresso${System.nanoTime()}", report = file)
  }
}

interface ITabPedido : ITabView {
  fun filtro(): FiltroPedidoNota
  fun updatePedidos(pedido: List<PedidoCapa>)
  fun predidoSelecionado(): List<PedidoCapa>
}