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
import br.com.astrosoft.produto.viewmodel.pedido.TabPedidoCDViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosPedCD(val viewModel: TabPedidoCDViewModel, val pedido: PedidoVenda) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoPedidoVenda::class.java, false)
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos da Pedido ${pedido.ordno} loja: ${pedido.loja}", toolBar = {
      button("Entregue") {
        val user = AppConfig.userLogin() as? UserSaci
        isVisible = user?.voltarCD == true || user?.admin == true
        icon = VaadinIcon.ARROW_RIGHT.create()
        onClick {
          viewModel.marcaEnt()
        }
      }
      textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.LAZY
        this.valueChangeTimeout = 1500
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.marcaEntProdutos(it.value)
            this@textField.value = ""
            this@textField.focus()
          }
        }
      }
      button("Imprime") {
        icon = VaadinIcon.PRINT.create()
        onClick {
          viewModel.salvaProdutos()
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
      val user = AppConfig.userLogin() as? UserSaci
      if (user?.voltarCD == true || user?.admin == true) {
        setSelectionMode(Grid.SelectionMode.MULTI)
      }

      produtoPedidoCodigo()
      produtoPedidoBarcode()
      produtoPedidoDescricao()
      produtoPedidoGrade()
      produtoPedidoLocalizacao()
      produtoPedidoQuantidade()
      produtoPedidoEstoque()

      this.setClassNameGenerator { produto ->
        if (produto.marca == EMarcaPedido.ENT.num) "entregue" else null
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoPedidoVenda> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = pedido.produtos(EMarcaPedido.CD)
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): ProdutoPedidoVenda? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { it.barcode == codigoBarra }
  }

  fun updateProduto(produto: ProdutoPedidoVenda) {
    gridDetail.dataProvider.refreshItem(produto)
  }

  fun produtosMarcados(): List<ProdutoPedidoVenda> {
    return gridDetail.dataProvider.fetchAll().filter { it.marca == EMarcaPedido.ENT.num }
  }
}