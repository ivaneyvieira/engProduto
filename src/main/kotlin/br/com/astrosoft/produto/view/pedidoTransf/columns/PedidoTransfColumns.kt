package br.com.astrosoft.produto.view.pedidoTransf.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.PedidoTransf
import com.github.mvysny.karibudsl.v10.isExpand
import com.vaadin.flow.component.grid.Grid

object PedidoTransfColumns {
  fun Grid<PedidoTransf>.colunaPedidoTransfLojaOrig() = columnGrid(PedidoTransf::lojaOrigem) {
    this.setHeader("Lj Orig")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfLojaDest() = columnGrid(PedidoTransf::lojaDestino) {
    this.setHeader("Lj Dest")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfLoja() = columnGrid(PedidoTransf::loja) {
    this.setHeader("Loja")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfNumero() = columnGrid(PedidoTransf::ordno) {
    this.setHeader("Pedido")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfChaveCD() = columnGrid(PedidoTransf::chaveNovaCD) {
    this.setHeader("Chave")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfCliente() = columnGrid(PedidoTransf::cliente) {
    this.setHeader("Cliente")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfData() = columnGrid(PedidoTransf::data) {
    this.setHeader("Data")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfVendedor() = columnGrid(PedidoTransf::vendedor) {
    this.setHeader("Vendedor")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfUsuarioNum() = columnGrid(PedidoTransf::userno) {
    this.setHeader("Num")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfUsuario() = columnGrid(PedidoTransf::usuario) {
    this.setHeader("Usuário")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfSituacaoPedido() = columnGrid(PedidoTransf::situacaoPedido) {
    this.setHeader("Situação")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfObsevacao() = columnGrid(PedidoTransf::observacaoLimpa) {
    this.setHeader("Observação")
    this.isExpand = true
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfValor() = columnGrid(PedidoTransf::totalProdutos) {
    this.setHeader("Valor")
  }

  fun Grid<PedidoTransf>.colunaPedidoTransfSituacao() = columnGrid(PedidoTransf::situacao) {
    this.setHeader("Situação")
  }
}