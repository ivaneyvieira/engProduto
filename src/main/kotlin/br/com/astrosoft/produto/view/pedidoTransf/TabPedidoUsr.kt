package br.com.astrosoft.produto.view.pedidoTransf

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.pedidoTransf.ITabPedidoTransfUsr
import br.com.astrosoft.produto.viewmodel.pedidoTransf.TabPedidoTransfUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabPedidoTransfUsr(viewModel: TabPedidoTransfUsrViewModel) : TabPanelUser(viewModel), ITabPedidoTransfUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::pedidoTransfReserva, "Reserva")
    columnGrid(UserSaci::pedidoTransfAutorizada, "Autorizada")
    columnGrid(UserSaci::pedidoTransfEnt, "Entregue")
    columnGrid(UserSaci::pedidoTransfRessu4, "Ressu4")
  }

  override fun FormUsuario.configFields() {
    verticalBlock("Menus") {
      checkBox("Reserva") {
        binder.bind(this, UserSaci::pedidoTransfReserva.name)
      }
      checkBox("Autorizada") {
        binder.bind(this, UserSaci::pedidoTransfAutorizada.name)
      }
      checkBox("Entregue") {
        binder.bind(this, UserSaci::pedidoTransfEnt.name)
      }
      checkBox("Ressu4") {
        binder.bind(this, UserSaci::pedidoTransfRessu4.name)
      }
    }
    verticalBlock("Filtros") {
      filtroImpressoraTodas(binder, UserSaci::impressoraTrans)
    }
  }
}