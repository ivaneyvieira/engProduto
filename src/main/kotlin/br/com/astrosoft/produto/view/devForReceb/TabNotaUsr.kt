package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaUsr
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabNotaUsr(viewModel: TabNotaUsrViewModel) : TabPanelUser(viewModel), ITabNotaUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::recebimentoNotaEntrada, "Entrada")
    columnGrid(UserSaci::devFor2NotaPendencia, "Pendencia")
    columnGrid(UserSaci::devFor2NotaNFD, "NFD")
    columnGrid(UserSaci::devFor2NotaColeta, "Coleta")
    columnGrid(UserSaci::notaNFDAberta, "NFD Aberta")
    columnGrid(UserSaci::devFor2NotaGarantia, "Garantia")
    columnGrid(UserSaci::devFor2NotaTransportadora, "Transportadora")
    columnGrid(UserSaci::devFor2NotaEmail, "E-Mail")
    columnGrid(UserSaci::devFor2NotaReposto, "Reposto")
    columnGrid(UserSaci::devFor2NotaAcerto, "Acerto")
    columnGrid(UserSaci::devFor2NotaAcertoPago, "Acerto Pago")
    columnGrid(UserSaci::devFor2NotaAjuste, "Ajuste")
  }

  override fun FormUsuario.configFields() {
    horizontalBlock {
      verticalBlock("Menu") {
        checkBox("Entrada") {
          binder.bind(this, UserSaci::recebimentoNotaEntrada.name)
        }

        checkBox("Pendencia") {
          binder.bind(this, UserSaci::devFor2NotaPendencia.name)
        }

        checkBox("Coleta") {
          binder.bind(this, UserSaci::devFor2NotaColeta.name)
        }

        checkBox("NFD") {
          binder.bind(this, UserSaci::devFor2NotaNFD.name)
        }

        checkBox("NFD Aberta") {
          binder.bind(this, UserSaci::notaNFDAberta.name)
        }

        checkBox("Garantia") {
          binder.bind(this, UserSaci::devFor2NotaGarantia.name)
        }

        checkBox("Transportadora") {
          binder.bind(this, UserSaci::devFor2NotaTransportadora.name)
        }

        checkBox("E-Mail") {
          binder.bind(this, UserSaci::devFor2NotaEmail.name)
        }

        checkBox("Reposto") {
          binder.bind(this, UserSaci::devFor2NotaReposto.name)
        }

        checkBox("Acerto") {
          binder.bind(this, UserSaci::devFor2NotaAcerto.name)
        }

        checkBox("Acerto Pago") {
          binder.bind(this, UserSaci::devFor2NotaAcertoPago.name)
        }

        checkBox("Ajuste") {
          binder.bind(this, UserSaci::devFor2NotaAjuste.name)
        }
      }
      verticalBlock("Filtros") {
        filtroLoja(binder, UserSaci::devFor2Loja)
      }
    }
  }
}
