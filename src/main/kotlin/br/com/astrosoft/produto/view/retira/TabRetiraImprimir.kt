package br.com.astrosoft.produto.view.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.retira.IPedidoRetiraImprimir
import br.com.astrosoft.produto.viewmodel.retira.PedidoRetiraImprimirViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.SortDirection.ASCENDING
import com.vaadin.flow.data.provider.SortDirection.DESCENDING
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabRetiraImprimir(val viewModel: PedidoRetiraImprimirViewModel) : TabPanelGrid<Pedido>(Pedido::class),
  IPedidoRetiraImprimir {
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

  override fun filtro(): FiltroPedido {
    return FiltroPedido(
      tipo = ETipoPedido.RETIRA,
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value ?: LocalDate.now(),
      dataFinal = edtDataFinal.value ?: LocalDate.now(),
    )
  }

  override fun isAuthorized(): Boolean {
    val userSaci = (AppConfig.userLogin() as? UserSaci) ?: return false
    return userSaci.retiraImprimir
  }

  override val label: String
    get() = "Imprimir"

  override fun updateComponent() {
    viewModel.updateGridImprimir()
  }

  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateGridImprimir()
      }
    }
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateGridImprimir()
      }
    }
    edtDataInicial = datePicker("Data inicial") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateGridImprimir()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateGridImprimir()
      }
    }
  }

  override fun Grid<Pedido>.gridPanel() {
    addColumnButton(VaadinIcon.PRINT, "Imprimir", "Imprimir") { pedido ->
      viewModel.confirmaPrint(pedido)
    }

    columnGrid(Pedido::tipoEcommece, "Tipo")
    columnGrid(Pedido::loja, "Loja")
    columnGrid(Pedido::pedido, "Pedido")

    columnGrid(Pedido::data, "Data")
    columnGrid(Pedido::hora, "Hora")

    columnGrid(Pedido::nfFat, "NF Fat")

    columnGrid(Pedido::dataFat, "Data")
    columnGrid(Pedido::horaFat, "Hora")
    columnGrid(Pedido::vendno, "Vendedor")

    columnGrid(Pedido::frete, "R$ Frete")
    columnGrid(Pedido::valorComFrete, "R$ Nota")
    columnGrid(Pedido::cliente, "Cliente").expand()

    this.sort(
      listOf(
        GridSortOrder(getColumnBy(Pedido::loja), ASCENDING),
        GridSortOrder(getColumnBy(Pedido::pedido), DESCENDING)
      )
    )
  }
}