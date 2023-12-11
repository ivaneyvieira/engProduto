package br.com.astrosoft.produto.view.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.Pedido
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.retira.IPedidoRetiraImprimir
import br.com.astrosoft.produto.viewmodel.retira.PedidoRetiraImprimirViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.SelectionMode
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.icon.VaadinIcon.*
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.SortDirection.ASCENDING
import com.vaadin.flow.data.provider.SortDirection.DESCENDING
import com.vaadin.flow.data.value.ValueChangeMode.TIMEOUT
import java.time.LocalDate
import java.util.*

class TabRetiraImprimir(val viewModel: PedidoRetiraImprimirViewModel) : TabPanelGrid<Pedido>(Pedido::class),
  IPedidoRetiraImprimir {
  private lateinit var edtPedidoImprimir: TextField
  private lateinit var edtRotaImprimir: TextField
  private lateinit var edtAreaImprimir: TextField
  private lateinit var edtDataImprimir: DatePicker
  override val label: String = "Imprimir"

  override fun isAuthorized(): Boolean {
    val userSaci = (AppConfig.userLogin() as? UserSaci) ?: return false
    return userSaci.retiraImprimir
  }

  override fun updateComponent() {
    viewModel.updateGridImprimir()
  }

  override val pedidoImprimir: Int
    get() = edtPedidoImprimir.value?.toIntOrNull() ?: 0
  override val dataImprimir: LocalDate?
    get() = edtDataImprimir.value
  override val areaImprimir: String
    get() = edtAreaImprimir.value.uppercase(Locale.getDefault())
  override val rotaImprimir: String
    get() = edtRotaImprimir.value.uppercase(Locale.getDefault())

  override fun HorizontalLayout.toolBarConfig() {
    button("Confirma") {
      icon = THUMBS_UP.create()
      addClickListener {
        viewModel.confirmaPrint()
      }
    }

    edtPedidoImprimir = textField("Numero Pedido") {
      this.valueChangeMode = TIMEOUT
      this.isAutofocus = true
      addValueChangeListener {
        viewModel.updateGridImprimir()
      }
    }
    edtDataImprimir = datePicker("Data") {
      localePtBr()
      isClearButtonVisible = true
      addValueChangeListener {
        viewModel.updateGridImprimir()
      }
    }
    edtAreaImprimir = textField("Área") {
      this.valueChangeMode = TIMEOUT
      addValueChangeListener {
        viewModel.updateGridImprimir()
      }
    }
    edtRotaImprimir = textField("Rota") {
      this.valueChangeMode = TIMEOUT

      addValueChangeListener {
        viewModel.updateGridImprimir()
      }
    }
  }

  override fun Grid<Pedido>.gridPanel() {
    setSelectionMode(SelectionMode.MULTI)
    columnGrid(Pedido::tipoEcommece, "Tipo")
    columnGrid(Pedido::loja, "Loja")
    columnGrid(Pedido::pedido, "Pedido")

    columnGrid(Pedido::dataHoraPrint, "Data Hora Impressão")
    columnGrid(Pedido::data, "Data")
    columnGrid(Pedido::hora, "Hora")

    columnGrid(Pedido::nfFat, "NF Fat")

    columnGrid(Pedido::dataFat, "Data")
    columnGrid(Pedido::horaFat, "Hora")
    columnGrid(Pedido::vendno, "Vendedor")

    columnGrid(Pedido::frete, "R$ Frete")
    columnGrid(Pedido::valorComFrete, "R$ Nota")
    columnGrid(Pedido::obs, "Obs")

    columnGrid(Pedido::nfEnt, "NF Ent")
    columnGrid(Pedido::dataEnt, "Data")
    columnGrid(Pedido::horaEnt, "Hora")

    columnGrid(Pedido::username, "Usuário")

    this.sort(
      listOf(
        GridSortOrder(getColumnBy(Pedido::loja), ASCENDING),
        GridSortOrder(getColumnBy(Pedido::pedido), DESCENDING)
      )
    )
  }
}