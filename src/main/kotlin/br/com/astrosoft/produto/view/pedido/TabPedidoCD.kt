package br.com.astrosoft.produto.view.pedido

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.pedido.columns.PedidoColumns.colunaPedidoCliente
import br.com.astrosoft.produto.view.pedido.columns.PedidoColumns.colunaPedidoData
import br.com.astrosoft.produto.view.pedido.columns.PedidoColumns.colunaPedidoLoja
import br.com.astrosoft.produto.view.pedido.columns.PedidoColumns.colunaPedidoNumero
import br.com.astrosoft.produto.view.pedido.columns.PedidoColumns.colunaPedidoVendedor
import br.com.astrosoft.produto.viewmodel.pedido.ITabPedidoCD
import br.com.astrosoft.produto.viewmodel.pedido.TabPedidoCDViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.data.value.ValueChangeMode

class TabPedidoCD(val viewModel: TabPedidoCDViewModel) : TabPanelGrid<PedidoVenda>(PedidoVenda::class), ITabPedidoCD {
  private var dlgProduto: DlgProdutosPedCD? = null
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
    colunaPedidoLoja()
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
      dlgProduto = DlgProdutosPedCD(viewModel, pedido)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaPedidoNumero()
    colunaPedidoData()
    colunaPedidoCliente()
    colunaPedidoVendedor()
  }

  override fun filtro(marca: EMarcaPedido): FiltroPedidoVenda {
    return FiltroPedidoVenda(storeno = edtLoja.value ?: 0, ordno = edtPedido.value ?: 0, marca = marca)
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

  override fun produtosMarcados(): List<ProdutoPedidoVenda> {
    return dlgProduto?.produtosMarcados().orEmpty()
  }

  override fun produtosCodigoBarras(codigoBarra: String): ProdutoPedidoVenda? {
    return dlgProduto?.produtosCodigoBarras(codigoBarra)
  }

  override fun findPedido(): PedidoVenda? {
    return dlgProduto?.pedido
  }

  override fun updateProduto(produto: ProdutoPedidoVenda) {
    dlgProduto?.updateProduto(produto)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.pedidoCD == true
  }

  override val label: String
    get() = "CD"

  override fun updateComponent() {
    viewModel.updateView()
  }
}