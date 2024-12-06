package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.FiltroPedidoRessuprimento
import br.com.astrosoft.produto.model.beans.PedidoRessuprimento
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.ressuprimento.ITabPedidoRessuprimento
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabPedidoRessuprimentoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabPedidoRessuprimento(val viewModel: TabPedidoRessuprimentoViewModel) :
  TabPanelGrid<PedidoRessuprimento>(PedidoRessuprimento::class), ITabPedidoRessuprimento {
  private var dlgProduto: DlgProdutosPedidoRessuprimento? = null
  private lateinit var edtPesquisa: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    button("Impressão") {
      this.icon = VaadinIcon.PRINT.create()
      addClickListener {
        viewModel.imprimePedido()
      }
    }

    button("Duplica") {
      this.icon = VaadinIcon.COPY.create()
      addClickListener {
        viewModel.duplicaPedido()
      }
    }

    button("Remove") {
      this.icon = VaadinIcon.TRASH.create()
      addClickListener {
        viewModel.removePedido()
      }
    }
  }

  override fun Grid<PedidoRessuprimento>.gridPanel() {
    columnGrid(PedidoRessuprimento::loja, "Loja")
    this.selectionMode = Grid.SelectionMode.MULTI

    addColumnButton(VaadinIcon.FILE_TABLE, "Produto", "Produto") {
      dlgProduto = DlgProdutosPedidoRessuprimento(viewModel, it)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }

    columnGrid(PedidoRessuprimento::data, "Data")
    columnGrid(PedidoRessuprimento::pedido, "Pedido")
    columnGrid(PedidoRessuprimento::vendno, "No Forn")
    columnGrid(PedidoRessuprimento::fornecedor, "Fornecedor", width = "400px")
    columnGrid(PedidoRessuprimento::totalPedido, "Total Pedido")
    columnGrid(PedidoRessuprimento::totalPendente, "Total Pendente")
    columnGrid(PedidoRessuprimento::observacao, "Observação", isExpand = true)
  }

  override fun filtro(): FiltroPedidoRessuprimento {
    return FiltroPedidoRessuprimento(
      pesquisa = edtPesquisa.value ?: "",
    )
  }

  override fun updatePedidos(pedido: List<PedidoRessuprimento>) {
    this.updateGrid(pedido)
  }

  override fun predidoSelecionado(): List<PedidoRessuprimento> {
    return itensSelecionados()
  }

  override fun produtosSelecionados(): List<ProdutoRessuprimento> {
    return dlgProduto?.produtosSelecionados() ?: emptyList()
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.pedidoRessuprimento == true
  }

  override val label: String
    get() = "Pedido"

  override fun updateComponent() {
    viewModel.updateView()
  }
}