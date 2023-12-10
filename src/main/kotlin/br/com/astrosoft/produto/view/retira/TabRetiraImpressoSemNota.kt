package br.com.astrosoft.produto.view.retira

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.Pedido
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.retira.IPedidoRetiraImpressoSemNota
import br.com.astrosoft.produto.viewmodel.retira.PedidoRetiraImpressoSemNotaViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.SelectionMode
import com.vaadin.flow.component.icon.VaadinIcon.CLOSE
import com.vaadin.flow.component.icon.VaadinIcon.EYE
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode.TIMEOUT

class TabRetiraImpressoSemNota(val viewModel: PedidoRetiraImpressoSemNotaViewModel) :
  TabPanelGrid<Pedido>(Pedido::class),
  IPedidoRetiraImpressoSemNota {
  private lateinit var edtPedidoImpressoSemNota: TextField
  override val label = "Impresso sem Nota"

  override fun updateComponent() {
    viewModel.updateGridImpressoSemNota()
  }

  override val pedidoImpressoSemNota: Int
    get() = edtPedidoImpressoSemNota.value?.toIntOrNull() ?: 0

  override fun isAuthorized(): Boolean {
    val userSaci = (AppConfig.userLogin() as? UserSaci) ?: return false
    return userSaci.retiraImpressoSemNota
  }

  override fun HorizontalLayout.toolBarConfig() {
    if (AppConfig.isAdmin) button("Desmarcar") {
      icon = CLOSE.create()
      addClickListener {
        viewModel.desmarcaSemNota()
      }
    }
    if (AppConfig.isAdmin) button("Visualizar") {
      icon = EYE.create()
      addClickListener {
        viewModel.imprimirPedidos(itensSelecionados())
      }
    }
    edtPedidoImpressoSemNota = textField("Numero Pedido") {
      this.valueChangeMode = TIMEOUT
      this.isAutofocus = true
      addValueChangeListener {
        updateComponent()
      }
    }
  }

  override fun Grid<Pedido>.gridPanel() {
    setSelectionMode(SelectionMode.MULTI)
    addColumnSeq("Seq")

    columnGrid(Pedido::tipoEcommece, "Tipo")
    columnGrid(Pedido::loja, "Loja")
    columnGrid(Pedido::pedido, "Pedido")

    columnGrid(Pedido::dataHoraPrint, "Data Hora Impressão")
    columnGrid(Pedido::data, "Data")
    columnGrid(Pedido::hora, "Hora")

    columnGrid(Pedido::nfFat, "NF Fat")

    columnGrid(Pedido::dataFat, "Data")
    columnGrid(Pedido::horaFat, "Hora")
    columnGrid(Pedido::loc, "CD")

    columnGrid(Pedido::piso, "Piso")
    columnGrid(Pedido::vendno, "Vendedor")
    columnGrid(Pedido::frete, "R$ Frete")

    columnGrid(Pedido::valorComFrete, "R$ Nota")
    columnGrid(Pedido::obs, "Obs")
    columnGrid(Pedido::nfEnt, "NF Ent")

    columnGrid(Pedido::dataEnt, "Data")
    columnGrid(Pedido::horaEnt, "Hora")
    columnGrid(Pedido::username, "Usuário")
  }
}