package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.recebimento.ITabRecebimentoUsr
import br.com.astrosoft.produto.viewmodel.recebimento.TabRecebimentoUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabRecebimentoUsr(viewModel: TabRecebimentoUsrViewModel) : TabPanelUser(viewModel), ITabRecebimentoUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::recebimentoPedido, "Pedidos")
    columnGrid(UserSaci::recebimentoAgenda, "Pré-entrada")
    columnGrid(UserSaci::recebimentoXML, "XML")
    columnGrid(UserSaci::recebimentoPreEnt, "Pre-Ent XML")
    columnGrid(UserSaci::recebimentoReceberNota, "ReceberNota")
    columnGrid(UserSaci::recebimentoNotaRecebida, "Nota Recebida")
  }

  override fun FormUsuario.configFields() {
    verticalBlock("Menu") {
      checkBox("Pedidos") {
        binder.bind(this, UserSaci::recebimentoPedido.name)
      }
      checkBox("Agenda") {
        binder.bind(this, UserSaci::recebimentoAgenda.name)
      }
      checkBox("XML") {
        binder.bind(this, UserSaci::recebimentoXML.name)
      }
      checkBox("Pre-Ent XML") {
        binder.bind(this, UserSaci::recebimentoPreEnt.name)
      }
      checkBox("Receber Nota") {
        binder.bind(this, UserSaci::recebimentoReceberNota.name)
      }
      checkBox("Nota Recebida") {
        binder.bind(this, UserSaci::recebimentoNotaRecebida.name)
      }
    }
    verticalBlock("Filtros") {
      filtroLoja(binder, UserSaci::lojaRec)
      filtroLocalizacao(binder, UserSaci::localizacaoRec)
      filtroImpressoraTermica(binder, UserSaci::impressoraRec)
    }
  }
}