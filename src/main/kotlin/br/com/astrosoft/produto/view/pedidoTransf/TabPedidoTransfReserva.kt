package br.com.astrosoft.produto.view.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfCliente
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfData
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfLojaDest
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfLojaOrig
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfNumero
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfObsevacao
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfSituacaoPedido
import br.com.astrosoft.produto.view.pedidoTransf.columns.PedidoTransfColumns.colunaPedidoTransfUsuario
import br.com.astrosoft.produto.viewmodel.pedidoTransf.ITabPedidoTransfReserva
import br.com.astrosoft.produto.viewmodel.pedidoTransf.TabPedidoTransfReservaViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabPedidoTransfReserva(val viewModel: TabPedidoTransfReservaViewModel) :
  TabPanelGrid<PedidoTransf>(PedidoTransf::class),
  ITabPedidoTransfReserva {
  private var dlgProduto: DlgProdutosPedTransfReserva? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  init {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isVisible = user?.storeno == 0
    cmbLoja.value = viewModel.findLoja(user?.storeno ?: 0) ?: Loja.lojaZero
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.impressoraTrans?.toList() ?: emptyList()
  }

  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
      }
    }
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data inicial") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<PedidoTransf>.gridPanel() {
    this.addClassName("styling")
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
      dlgProduto = DlgProdutosPedTransfReserva(viewModel, pedido)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaPedidoTransfLojaOrig()
    colunaPedidoTransfLojaDest()
    colunaPedidoTransfCliente()
    colunaPedidoTransfData()
    colunaPedidoTransfNumero()
    addColumnButton(VaadinIcon.SIGN_IN, "Autoriza", "Autoriza") { pedido ->
      viewModel.formAutoriza(pedido)
    }
    colunaPedidoTransfUsuario()
    colunaPedidoTransfSituacaoPedido()
    colunaPedidoTransfObsevacao()
  }

  override fun formAutoriza(pedido: PedidoTransf) {
    val form = FormAutoriza(pedido)
    DialogHelper.showForm(caption = "Autoriza pedido", form = form) {
      viewModel.autorizaPedido(pedido, form.login, form.senha)
    }
  }

  override fun filtro(marca: EMarcaPedido): FiltroPedidoTransf {
    return FiltroPedidoTransf(
      storeno = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      marca = marca,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      autorizado = false,
      impresso = false,
    )
  }

  override fun updatePedidos(pedidos: List<PedidoTransf>) {
    updateGrid(pedidos)
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelcionados(): List<ProdutoPedidoTransf> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun produtosMarcados(): List<ProdutoPedidoTransf> {
    return dlgProduto?.produtosMarcados().orEmpty()
  }

  override fun produtosCodigoBarras(codigoBarra: String): ProdutoPedidoTransf? {
    return dlgProduto?.produtosCodigoBarras(codigoBarra)
  }

  override fun findPedido(): PedidoTransf? {
    return dlgProduto?.pedido
  }

  override fun updateProduto(produto: ProdutoPedidoTransf) {
    dlgProduto?.updateProduto(produto)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.pedidoTransfReserva == true
  }

  override val label: String
    get() = "Reserva"

  override fun updateComponent() {
    viewModel.updateView()
  }
}