package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.FiltroPedidoCapa
import br.com.astrosoft.produto.model.beans.Pedido
import br.com.astrosoft.produto.model.beans.PedidoCapa
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.recebimento.ITabPedido
import br.com.astrosoft.produto.viewmodel.recebimento.TabPedidoViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabPedido(val viewModel: TabPedidoViewModel) :
  TabPanelGrid<PedidoCapa>(PedidoCapa::class), ITabPedido {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker


  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000
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
  }

  override fun Grid<PedidoCapa>.gridPanel() {
    columnGrid(PedidoCapa::loja, "Loja")
    columnGrid(PedidoCapa::pedido, "Pedido")
    columnGrid(PedidoCapa::data, "Data")
    columnGrid(PedidoCapa::no, "No")
    columnGrid(PedidoCapa::fornecedor, "Fornecedor")
    columnGrid(PedidoCapa::total, "Total")
  }

  override fun filtro(): FiltroPedidoCapa {
    return FiltroPedidoCapa(
      loja = 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value
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