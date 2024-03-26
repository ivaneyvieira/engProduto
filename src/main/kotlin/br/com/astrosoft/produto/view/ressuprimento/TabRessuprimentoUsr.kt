package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.ressuprimento.ITabRessuprimentoUsr
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoUsrViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
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

    verticalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = false

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
      checkBox("Usuário Recebedor") {
        binder.bind(this, UserSaci::ressuprimentoRecebedor.name)
      }
    }
    verticalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = false

      select<Int>("Nome Loja") {
        this.setWidthFull()
        val lojas = viewModel.findAllLojas()
        val lojasNum = lojas.map { it.no } + listOf(0)
        setItems(lojasNum.distinct().sorted())
        this.isEmptySelectionAllowed = true
        this.setItemLabelGenerator { storeno ->
          when (storeno) {
            0    -> "Todas as lojas"
            else -> lojas.firstOrNull { loja ->
              loja.no == storeno
            }?.descricao ?: ""
          }
        }
        binder.bind(this, UserSaci::lojaRessu.name)
      }
      multiSelectComboBox<String>("Impressora") {
        this.setWidthFull()
        setItems(listOf("TODAS") + viewModel.allTermica().map { it.name })
        binder.bind(this, UserSaci::impressoraRessu.name)
      }
      multiSelectComboBox<String>("Localização") {
        this.setWidthFull()
        setItems(listOf("TODOS") + viewModel.allLocalizacao())
        binder.bind(this, UserSaci::listaRessuprimento.name)
      }
    }
  }
}