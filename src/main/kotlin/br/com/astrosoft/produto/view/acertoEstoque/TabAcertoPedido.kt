package br.com.astrosoft.produto.view.acertoEstoque

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.PedidoAcerto
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.acertoEstoque.ITabAcertoPedido
import br.com.astrosoft.produto.viewmodel.acertoEstoque.TabAcertoPedidoViewModel
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class TabAcertoPedido(val viewModel: TabAcertoPedidoViewModel) :
  TabPanelGrid<PedidoAcerto>(PedidoAcerto::class),
  ITabAcertoPedido {
  private var dlgProduto: DlgProdutosAcerto? = null

  override fun HorizontalLayout.toolBarConfig() {
    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "produtoAcerto") {
      viewModel.geraPlanilha()
    }
  }

  override fun Grid<PedidoAcerto>.gridPanel() {
    this.addClassName("styling")
    this.selectionMode = Grid.SelectionMode.MULTI

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
      dlgProduto = DlgProdutosAcerto(viewModel, pedido)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(PedidoAcerto::data, header = "Data")
    columnGrid(PedidoAcerto::pedido, header = "Pedido")
    columnGrid(PedidoAcerto::vendno, header = "No Forn")
    columnGrid(PedidoAcerto::fornecedor, header = "Fornecedor")
    columnGrid(PedidoAcerto::totalPedido, header = "Total Pedido")
    columnGrid(PedidoAcerto::totalPendente, header = "Total Pendente")
    columnGrid(PedidoAcerto::observacao, header = "Observação")
  }

  override fun updatePedido(pedidos: List<PedidoAcerto>) {
    updateGrid(pedidos)
  }

  override fun pedidoSelecionado(): List<PedidoAcerto> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.acertoPedido == true
  }

  override val label: String
    get() = "Pedido"

  override fun updateComponent() {
    viewModel.updateView()
  }
}