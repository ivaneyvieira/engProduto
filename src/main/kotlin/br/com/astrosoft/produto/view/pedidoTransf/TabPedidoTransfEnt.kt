package br.com.astrosoft.produto.view.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfChaveCD
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfCliente
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfData
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfLoja
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfNumero
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfVendedor
import br.com.astrosoft.produto.viewmodel.pedidoTransf.ITabPedidoTransfEnt
import br.com.astrosoft.produto.viewmodel.pedidoTransf.TabPedidoTransfEntViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.data.value.ValueChangeMode

class TabPedidoTransfEnt(val viewModel: TabPedidoTransfEntViewModel) : TabPanelGrid<PedidoVenda>(PedidoVenda::class),
        ITabPedidoTransfEnt {
  private var dlgProduto: DlgProdutosPedTransfEnt? = null
  private lateinit var edtLoja: IntegerField
  private lateinit var edtPedido: IntegerField

  override fun HorizontalLayout.toolBarConfig() {
    edtLoja = integerField("Loja") {
      val user = AppConfig.userLogin() as? UserSaci
      isVisible = user?.storeno == 0
      value = user?.storeno
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtPedido = integerField("Pedido") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<PedidoVenda>.gridPanel() {
    colunaPedidoTransfLoja()
    addColumnButton(VaadinIcon.PRINT, "Etiqueta", "Etiqueta") { pedido ->
      viewModel.printEtiqueta(pedido)
    }
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
      dlgProduto = DlgProdutosPedTransfEnt(viewModel, pedido)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaPedidoTransfChaveCD()
    colunaPedidoTransfNumero()
    colunaPedidoTransfData()
    colunaPedidoTransfCliente()
    colunaPedidoTransfVendedor()
  }

  override fun filtro(marca: EMarcaPedido): FiltroPedido {
    return FiltroPedido(storeno = edtLoja.value ?: 0, ordno = edtPedido.value ?: 0, marca = marca)
  }

  override fun updatePedidos(pedidos: List<PedidoVenda>) {
    updateGrid(pedidos)
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelcionados(): List<ProdutoPedidoVenda> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.pedidoTransfEnt == true
  }

  override val label: String
    get() = "Entregue"

  override fun updateComponent() {
    viewModel.updateView()
  }
}