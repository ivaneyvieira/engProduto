package br.com.astrosoft.produto.view.pedido

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.view.TabPanelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.pedido.columns.PedidoColumns.colunaPedidoChaveCD
import br.com.astrosoft.produto.view.pedido.columns.PedidoColumns.colunaPedidoCliente
import br.com.astrosoft.produto.view.pedido.columns.PedidoColumns.colunaPedidoData
import br.com.astrosoft.produto.view.pedido.columns.PedidoColumns.colunaPedidoLoja
import br.com.astrosoft.produto.view.pedido.columns.PedidoColumns.colunaPedidoNumero
import br.com.astrosoft.produto.view.pedido.columns.PedidoColumns.colunaPedidoVendedor
import br.com.astrosoft.produto.viewmodel.pedido.ITabPedidoEnt
import br.com.astrosoft.produto.viewmodel.pedido.TabPedidoEntViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.data.value.ValueChangeMode

class TabPedidoEnt(val viewModel: TabPedidoEntViewModel) : TabPanelGrid<PedidoVenda>(PedidoVenda::class),
        ITabPedidoEnt {
  private var dlgProduto: DlgProdutosPedEnt? = null
  private lateinit var edtLoja: IntegerField
  private lateinit var edtPedido: IntegerField

  override fun HorizontalLayout.toolBarConfig() {
    edtLoja = integerField("Loja") {
      val user = Config.user as? UserSaci
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
    colunaPedidoLoja()
    addColumnButton(VaadinIcon.PRINT, "Etiqueta", "Etiqueta") { pedido ->
      viewModel.printEtiqueta(pedido)
    }
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
      dlgProduto = DlgProdutosPedEnt(viewModel, pedido)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaPedidoChaveCD()
    colunaPedidoNumero()
    colunaPedidoData()
    colunaPedidoCliente()
    colunaPedidoVendedor()
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

  override fun isAuthorized(user: IUser): Boolean {
    val username = user as? UserSaci
    return username?.pedidoEnt == true
  }

  override val label: String
    get() = "Entregue"

  override fun updateComponent() {
    viewModel.updateView()
  }
}