package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.ressuprimento.ITabRessuprimentoUsr
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabRessuprimentoUsr(viewModel: TabRessuprimentoUsrViewModel) : TabPanelUser(viewModel), ITabRessuprimentoUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::ressuprimentoCD, "Separar")
    columnGrid(UserSaci::ressuprimentoSep, "Separado")
    columnGrid(UserSaci::ressuprimentoEnt, "Entrege")
    columnGrid(UserSaci::ressuprimentoPen, "Pendente")
    columnGrid(UserSaci::ressuprimentoRec, "Recebido")
    columnGrid(UserSaci::ressuprimentoRecebedor, "Recebedor")
  }

  override fun FormUsuario.configFields() {
    horizontalBlock {
      verticalBlock("Menu") {
        checkBox("Separar") {
          binder.bind(this, UserSaci::ressuprimentoCD.name)
        }
        checkBox("Separado") {
          binder.bind(this, UserSaci::ressuprimentoSep.name)
        }
        checkBox("Entrege") {
          binder.bind(this, UserSaci::ressuprimentoEnt.name)
        }
        checkBox("Pendente") {
          binder.bind(this, UserSaci::ressuprimentoPen.name)
        }
        checkBox("Recebido") {
          binder.bind(this, UserSaci::ressuprimentoRec.name)
        }
        checkBox("Exclui") {
          binder.bind(this, UserSaci::ressuprimentoExclui.name)
        }
      }
      verticalBlock("Comandos") {
        checkBox("Usuário Recebedor") {
          binder.bind(this, UserSaci::ressuprimentoRecebedor.name)
        }
      }
    }
    verticalBlock("Filtros") {
      filtroLoja(binder, UserSaci::lojaRessu)
      filtroImpressoraTermica(binder, UserSaci::impressoraRessu)
      filtroLocalizacao(binder, UserSaci::listaRessuprimento)
    }
  }
}