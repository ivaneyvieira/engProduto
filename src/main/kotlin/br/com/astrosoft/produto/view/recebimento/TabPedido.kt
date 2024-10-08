package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.recebimento.ITabPedido
import br.com.astrosoft.produto.viewmodel.recebimento.TabPedidoViewModel
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

class TabPedido(val viewModel: TabPedidoViewModel) :
  TabPanelGrid<PedidoCapa>(PedidoCapa::class), ITabPedido {
  private var dlgProduto: DlgProdutosPedido? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbStatus: Select<EPedidosStatus>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaRec != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaRec ?: 0) ?: Loja.lojaZero
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
    init()
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    cmbStatus = select("Situação") {
      this.setItems(EPedidosStatus.entries)
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      this.value = EPedidosStatus.TODOS
      this.addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data Inicial") {
      this.value = LocalDate.now()
      this.localePtBr()
      this.addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.value = LocalDate.now()
      this.localePtBr()
      this.addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<PedidoCapa>.gridPanel() {
    columnGrid(PedidoCapa::loja, "Loja")
    columnGrid(PedidoCapa::pedido, "Pedido")
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { pedido ->
      dlgProduto = DlgProdutosPedido(viewModel, pedido)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    columnGrid(PedidoCapa::data, "Data")
    columnGrid(PedidoCapa::statusPedido, "Situação")
    columnGrid(PedidoCapa::no, "No")
    columnGrid(PedidoCapa::fornecedor, "Fornecedor", width = "400px")
    columnGrid(PedidoCapa::totalProduto, "Total Pedido")
    columnGrid(PedidoCapa::totalProdutoPendente, "Total Pendente")
    columnGrid(PedidoCapa::dataEmissao, "Emissão")
    columnGrid(PedidoCapa::dataEntrada, "Entrada")
    columnGrid(PedidoCapa::nfEntrada, "NF", width = "100px")
  }

  override fun filtro(): FiltroPedidoCapa {
    return FiltroPedidoCapa(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      status = cmbStatus.value ?: EPedidosStatus.TODOS,
    )
  }

  override fun updatePedidos(pedido: List<PedidoCapa>) {
    this.updateGrid(pedido)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.recebimentoPedido == true
  }

  override val label: String
    get() = "Pedido"

  override fun updateComponent() {
    viewModel.updateView()
  }
}