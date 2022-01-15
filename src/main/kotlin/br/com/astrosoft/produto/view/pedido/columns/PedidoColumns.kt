package br.com.astrosoft.produto.view.pedido.columns

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnLocalDate
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.PedidoVenda
import com.vaadin.flow.component.grid.Grid

object PedidoColumns {
  fun Grid<PedidoVenda>.colunaPedidoLoja() = addColumnInt(PedidoVenda::loja) {
    this.setHeader("Loja")
  }

  fun Grid<PedidoVenda>.colunaPedidoNumero() = addColumnInt(PedidoVenda::ordno) {
    this.setHeader("Pedido")
  }

  fun Grid<PedidoVenda>.colunaPedidoChaveCD() = addColumnString(PedidoVenda::chaveCD) {
    this.setHeader("Chave")
  }

  fun Grid<PedidoVenda>.colunaPedidoCliente() = addColumnInt(PedidoVenda::cliente) {
    this.setHeader("Cliente")
  }

  fun Grid<PedidoVenda>.colunaPedidoData() = addColumnLocalDate(PedidoVenda::data) {
    this.setHeader("Data")
  }

  fun Grid<PedidoVenda>.colunaPedidoVendedor() = addColumnInt(PedidoVenda::vendedor) {
    this.setHeader("Vendedor")
  }

  fun Grid<PedidoVenda>.colunaPedidoValor() = addColumnDouble(PedidoVenda::totalProdutos) {
    this.setHeader("Valor")
  }

  fun Grid<PedidoVenda>.colunaPedidoSituacao() = addColumnString(PedidoVenda::situacao) {
    this.setHeader("Situação")
  }
}