package br.com.astrosoft.produto.view.pedido

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.produto.model.beans.EMarcaPedido
import br.com.astrosoft.produto.model.beans.PedidoVenda
import br.com.astrosoft.produto.model.beans.ProdutoPedidoVenda
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoBarcode
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoCodigo
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoDescricao
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoEstoque
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoGrade
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoLocalizacao
import br.com.astrosoft.produto.view.pedido.columns.ProdutoPedViewColumns.produtoPedidoQuantidade
import br.com.astrosoft.produto.viewmodel.pedido.TabPedidoEntViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgProdutosPedEnt(val viewModel: TabPedidoEntViewModel, val pedido: PedidoVenda) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoPedidoVenda::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos do pedido ${pedido.ordno} loja: ${pedido.loja}", toolBar = {
      button("Volta") {
        val user = AppConfig.userLogin() as? UserSaci
        isVisible = user?.voltarEnt == true || user?.admin == true
        icon = VaadinIcon.ARROW_LEFT.create()
        onClick {
          viewModel.marcaCD()
          gridDetail.setItems(pedido.produtos(EMarcaPedido.ENT))
        }
      }
    }, onClose = {
      onClose()
    }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form?.open()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)
      produtoPedidoCodigo()
      produtoPedidoBarcode()
      produtoPedidoDescricao()
      produtoPedidoGrade()
      produtoPedidoLocalizacao()
      produtoPedidoQuantidade()
      produtoPedidoEstoque()
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoPedidoVenda> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = pedido.produtos(EMarcaPedido.ENT)
    gridDetail.setItems(listProdutos)
  }
}