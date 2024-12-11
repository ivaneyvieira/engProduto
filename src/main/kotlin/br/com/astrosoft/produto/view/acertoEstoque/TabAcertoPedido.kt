package br.com.astrosoft.produto.view.acertoEstoque

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.PedidoAcerto
import br.com.astrosoft.produto.model.beans.ProdutoAcerto
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ressuprimento.FormLogin
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

    addColumnButton(VaadinIcon.PRINT, "Preview", "Preview") { pedido ->
      viewModel.previewPedido(pedido)
    }

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
      dlgProduto = DlgProdutosAcerto(viewModel, pedido)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(PedidoAcerto::data, header = "Data")
    columnGrid(PedidoAcerto::loja, header = "Loja")
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

  override fun produtosSelecionados(): List<ProdutoAcerto> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun confirmaLogin(
    msg: String,
    permissao: UserSaci.() -> Boolean,
    onLogin: () -> Unit
  ) {
    val formLogin = FormLogin(msg)

    DialogHelper.showForm("Confirmação", formLogin) {
      val user = UserSaci.findUser(formLogin.login)
      if (user.any { it.senha == formLogin.senha }) {
        if (user.any { it.permissao() }) {
          onLogin()
        } else {
          DialogHelper.showError("Usuário não tem permissão para esta operação")
        }
      } else {
        DialogHelper.showError("Usuário ou senha inválidos")
      }
    }
  }

  override fun updateProdutos() {
    dlgProduto?.update()
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

  override fun pedidoDialog(): PedidoAcerto? {
    return dlgProduto?.pedido
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    val impressoraRessu = user?.impressoraAcerto ?: return emptyList()
    return if (impressoraRessu.contains("TODOS")) emptyList() else impressoraRessu.toList()
  }
}