package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.buttonPlanilha
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.*
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

    this.buttonPlanilha("Planilha", VaadinIcon.FILE_TABLE.create(), "produtoRessuprimento") {
      viewModel.geraPlanilha()
    }

    button("Copia Pedido") {
      this.icon = VaadinIcon.COPY.create()
      addClickListener {
        viewModel.copiaPedido()
      }
    }
  }

  override fun Grid<PedidoRessuprimento>.gridPanel() {
    this.selectionMode = Grid.SelectionMode.MULTI

    addColumnButton(VaadinIcon.PRINT, "Preview", "Preview") { pedido ->
      viewModel.previewPedido(pedido)
    }

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

  override fun pedidoSelecionado(): List<PedidoRessuprimento> {
    return itensSelecionados()
  }

  override fun produtosSelecionados(): List<ProdutoRessuprimento> {
    return dlgProduto?.produtosSelecionados() ?: emptyList()
  }

  override fun updateProdutos() {
    dlgProduto?.update()
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

  override fun formCopiaPedido(block: (beanCopia: BeanCopia) -> Unit) {
    val formCopia = FormCopiaPedido()
    DialogHelper.showForm("Copia Pedido", formCopia) {
      val beanCopia = formCopia.getBeanCopia()
      if (beanCopia != null) {
        block(beanCopia)
      } else {
        DialogHelper.showError("Preencha todos os campos")
      }
    }
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

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    val impressoraRessu = user?.impressoraRessu ?: return emptyList()
    return if (impressoraRessu.contains("TODOS")) emptyList() else impressoraRessu.toList()
  }
}

