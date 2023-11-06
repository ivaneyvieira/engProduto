package br.com.astrosoft.produto.view.pedidoTransf.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.PedidoVenda
import com.vaadin.flow.component.grid.Grid

object PedidoTransfColumns {
  fun Grid<PedidoVenda>.colunaPedidoTransfLoja() = columnGrid(PedidoVenda::loja) {
    this.setHeader("Loja")
  }

  fun Grid<PedidoVenda>.colunaPedidoTransfNumero() = columnGrid(PedidoVenda::ordno) {
    this.setHeader("Pedido")
  }

  fun Grid<PedidoVenda>.colunaPedidoTransfChaveCD() = columnGrid(PedidoVenda::chaveNovaCD) {
    this.setHeader("Chave")
  }

  fun Grid<PedidoVenda>.colunaPedidoTransfCliente() = columnGrid(PedidoVenda::cliente) {
    this.setHeader("Cliente")
  }

  fun Grid<PedidoVenda>.colunaPedidoTransfData() = columnGrid(PedidoVenda::data) {
    this.setHeader("Data")
  }

  fun Grid<PedidoVenda>.colunaPedidoTransfVendedor() = columnGrid(PedidoVenda::vendedor) {
    this.setHeader("Vendedor")
  }

  fun Grid<PedidoVenda>.colunaPedidoTransfValor() = columnGrid(PedidoVenda::totalProdutos) {
    this.setHeader("Valor")
  }

  fun Grid<PedidoVenda>.colunaPedidoTransfSituacao() = columnGrid(PedidoVenda::situacao) {
    this.setHeader("Situação")
  }
}