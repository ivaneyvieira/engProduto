package br.com.astrosoft.produto.view.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.pedidoTransf.columns.ProdutoPedTransfViewColumns.produtoPedidoTransfCodigo
import br.com.astrosoft.produto.view.pedidoTransf.columns.ProdutoPedTransfViewColumns.produtoPedidoTransfDescricao
import br.com.astrosoft.produto.view.pedidoTransf.columns.ProdutoPedTransfViewColumns.produtoPedidoTransfGrade
import br.com.astrosoft.produto.view.pedidoTransf.columns.ProdutoPedTransfViewColumns.produtoPedidoTransfQuantidade
import br.com.astrosoft.produto.viewmodel.pedidoTransf.TabPedidoTransfReservaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select

class DlgProdutosPedTransfReserva(val viewModel: TabPedidoTransfReservaViewModel, val pedido: PedidoTransf) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoPedidoTransf::class.java, false)
  private var cmbImpressora: Select<Impressora>? = null
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Pedido ${pedido.ordno} - ${pedido.rota}", toolBar = {
      cmbImpressora = select<Impressora>("Impressora") {
        val lista =Impressora.all()
        val printerUser = (AppConfig.userLogin() as? UserSaci)?.impressora ?: ""
        setItems(lista)
        this.setItemLabelGenerator { it.name }

        this.value = lista.firstOrNull {
          it.name == printerUser
        }
      }
      this.button("Imprimir") {
        this.onLeftClick {
          val impressora = cmbImpressora?.value?.name ?: "Nenhuma impressora selecionada"
          viewModel.imprimePedido(pedido, impressora)
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

      produtoPedidoTransfCodigo()
      produtoPedidoTransfDescricao()
      produtoPedidoTransfGrade()
      produtoPedidoTransfQuantidade()

      this.setClassNameGenerator {
        if (it.marca == EMarcaPedido.ENT.num) "entregue" else null
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoPedidoTransf> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = pedido.produtos()
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): ProdutoPedidoTransf? {
    return gridDetail.dataProvider.fetchAll().firstOrNull { it.barcode == codigoBarra }
  }

  fun updateProduto(produto: ProdutoPedidoTransf) {
    gridDetail.dataProvider.refreshItem(produto)
  }

  fun produtosMarcados(): List<ProdutoPedidoTransf> {
    return gridDetail.dataProvider.fetchAll().filter { it.marca == EMarcaPedido.ENT.num }
  }
}