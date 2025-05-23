package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.recebimento.ITabPedido
import br.com.astrosoft.produto.viewmodel.recebimento.TabPedidoViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.checkbox.Checkbox
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
  private var dlgProduto: DlgNotaPedido? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var chkSemRecebimento: Checkbox
  private lateinit var cmbPreEnt: Select<EPreEntrada>
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
    verticalLayout {
      this.isPadding = false
      this.isSpacing = false
      this.isMargin = false
      horizontalLayout {
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
        cmbPreEnt = select("Pré-Ent") {
          this.setItems(EPreEntrada.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = EPreEntrada.TODOS
          this.addValueChangeListener {
            viewModel.updateView()
          }
        }
        chkSemRecebimento = checkBox("Sem Recebimento") {
          this.value = false
          this.addValueChangeListener {
            viewModel.updateView()
          }
        }
      }

      horizontalLayout {
        edtPesquisa = textField("Pesquisa") {
          this.width = "300px"
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
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
        button("Impressão") {
          this.icon = VaadinIcon.PRINT.create()
          addClickListener {
            viewModel.imprimePedido()
          }
        }
      }
    }
  }

  override fun Grid<PedidoCapa>.gridPanel() {
    columnGrid(PedidoCapa::loja, "Loja")
    addColumnButton(VaadinIcon.FILE_TABLE, "Notas", "Notas") { pedido ->
      if (pedido.notas.isEmpty()) {
        val dlgProdutoCompra = DlgPedidoProdutoCompra(viewModel, pedido)
        dlgProdutoCompra.showDialog {
          viewModel.updateView()
        }
      } else {
        dlgProduto = DlgNotaPedido(viewModel, pedido)
        dlgProduto?.showDialog {
          viewModel.updateView()
        }
      }
    }
    this.selectionMode = Grid.SelectionMode.MULTI

    columnGrid(PedidoCapa::data, "Data")
    columnGrid(PedidoCapa::pedido, "Pedido")
    columnGrid(PedidoCapa::no, "No Forn")
    columnGrid(PedidoCapa::fornecedor, "Fornecedor", width = "250px")
    columnGrid(PedidoCapa::totalPedido, "Total Pedido")
    columnGrid(PedidoCapa::totalPendente, "Total Pendente")
    columnGrid(PedidoCapa::totalRecebido, "Total Recebido")
    columnGrid(PedidoCapa::preEntrada, "Pré-Ent")
    columnGrid(PedidoCapa::observacao, "Observação", isExpand = true)
  }

  override fun filtro(): FiltroPedidoNota {
    return FiltroPedidoNota(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      status = EPedidosStatus.TODOS,
      preEntrada = cmbPreEnt.value ?: EPreEntrada.TODOS,
      semRecebimento = chkSemRecebimento.value ?: false
    )
  }

  override fun updatePedidos(pedido: List<PedidoCapa>) {
    this.updateGrid(pedido)
  }

  override fun predidoSelecionado(): List<PedidoCapa> {
    return itensSelecionados()
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