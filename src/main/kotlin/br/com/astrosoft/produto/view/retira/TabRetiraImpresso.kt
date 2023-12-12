package br.com.astrosoft.produto.view.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.expand
import br.com.astrosoft.produto.model.beans.Pedido
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.retira.IPedidoRetiraImpresso
import br.com.astrosoft.produto.viewmodel.retira.PedidoRetiraImpressoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.SelectionMode
import com.vaadin.flow.component.icon.VaadinIcon.CLOSE
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode.TIMEOUT

class TabRetiraImpresso(val viewModel: PedidoRetiraImpressoViewModel) :
  TabPanelGrid<Pedido>(Pedido::class),
  IPedidoRetiraImpresso {
  private lateinit var edtPedidoImpresso: TextField
  override val label = "Impresso"

  override fun updateComponent() {
    viewModel.updateGridImpresso()
  }

  override val pedidoImpressoa: Int
    get() = edtPedidoImpresso.value?.toIntOrNull() ?: 0

  override fun isAuthorized(): Boolean {
    val userSaci = (AppConfig.userLogin() as? UserSaci) ?: return false
    return userSaci.retiraImpresso
  }

  override fun HorizontalLayout.toolBarConfig() {
    if (AppConfig.isAdmin) button("Desmarcar") {
      icon = CLOSE.create()
      addClickListener {
        viewModel.desmarcaSemNota()
      }
    }

    edtPedidoImpresso = textField("Numero Pedido") {
      this.valueChangeMode = TIMEOUT
      this.isAutofocus = true
      addValueChangeListener {
        updateComponent()
      }
    }
  }

  override fun Grid<Pedido>.gridPanel() {
    setSelectionMode(SelectionMode.MULTI)

    columnGrid(Pedido::tipoEcommece, "Tipo")
    columnGrid(Pedido::loja, "Loja")
    columnGrid(Pedido::pedido, "Pedido")
    columnGrid(Pedido::dataHoraPrint, "Impressão")

    columnGrid(Pedido::data, "Data")
    columnGrid(Pedido::hora, "Hora")

    columnGrid(Pedido::nfFat, "NF Fat")

    columnGrid(Pedido::dataFat, "Data")
    columnGrid(Pedido::horaFat, "Hora")
    columnGrid(Pedido::vendno, "Vendedor")

    columnGrid(Pedido::frete, "R$ Frete")
    columnGrid(Pedido::valorComFrete, "R$ Nota")
    columnGrid(Pedido::cliente, "Cliente").expand()

  }
}