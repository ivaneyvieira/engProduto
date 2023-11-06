package br.com.astrosoft.produto.view.pedido.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.PedidoVenda
import com.vaadin.flow.component.grid.Grid

object PedidoColumns {
  fun Grid<PedidoVenda>.colunaPedidoLoja() = columnGrid(PedidoVenda::loja) {
    this.setHeader("Loja")
  }

  fun Grid<PedidoVenda>.colunaPedidoNumero() = columnGrid(PedidoVenda::ordno) {
    this.setHeader("Pedido")
  }

  fun Grid<PedidoVenda>.colunaPedidoChaveCD() = columnGrid(PedidoVenda::chaveNovaCD) {
    this.setHeader("Chave")
  }

  fun Grid<PedidoVenda>.colunaPedidoCliente() = columnGrid(PedidoVenda::cliente) {
    this.setHeader("Cliente")
  }

  fun Grid<PedidoVenda>.colunaPedidoData() = columnGrid(PedidoVenda::data) {
    this.setHeader("Data")
  }

  fun Grid<PedidoVenda>.colunaPedidoVendedor() = columnGrid(PedidoVenda::vendedor) {
    this.setHeader("Vendedor")
  }

  fun Grid<PedidoVenda>.colunaPedidoValor() = columnGrid(PedidoVenda::totalProdutos) {
    this.setHeader("Valor")
  }

  fun Grid<PedidoVenda>.colunaPedidoSituacao() = columnGrid(PedidoVenda::situacao) {
    this.setHeader("Situação")
  }
}