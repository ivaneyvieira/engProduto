package br.com.astrosoft.produto.view.notaSaida

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.ETipoNota
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.notaSaida.ITabNotaUsr
import br.com.astrosoft.produto.viewmodel.notaSaida.TabNotaUsrViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
import com.vaadin.flow.component.grid.Grid

class TabNotaUsr(viewModel: TabNotaUsrViewModel) : TabPanelUser(viewModel), ITabNotaUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::notaExp, "Exp")
    columnGrid(UserSaci::notaCD, "CD")
    columnGrid(UserSaci::notaEnt, "Entregue")
    columnGrid({
      ETipoNota.entries.firstOrNull { et -> et.num == it.tipoNota }?.descricao ?: ""
    }, "Nota")
  }

  override fun FormUsuario.configFields() {
    verticalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = true

      em("Paineis")

      checkBox("Exp") {
        binder.bind(this, UserSaci::notaExp.name)
      }
      checkBox("CD") {
        binder.bind(this, UserSaci::notaCD.name)
      }
      checkBox("Entregue") {
        binder.bind(this, UserSaci::notaEnt.name)
      }
      checkBox("Nota Entrega") {
        binder.bind(this, UserSaci::entregaNota.name)
      }
    }
    verticalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = false
      select<Int>("Loja") {
        this.setWidthFull()
        val lojas = viewModel.findAllLojas()
        val lojasNum = lojas.map { it.no } + listOf(0)
        setItems(lojasNum.distinct().sorted())
        this.setItemLabelGenerator { storeno ->
          when (storeno) {
            0    -> "Todas as lojas"
            else -> lojas.firstOrNull { loja ->
              loja.no == storeno
            }?.descricao ?: ""
          }
        }
        binder.bind(this, UserSaci::lojaNota.name)
      }
      multiSelectComboBox<String>("Impressora") {
        this.setWidthFull()
        setItems(viewModel.allEtiqueta().map { it.name })
        binder.bind(this, UserSaci::impressoraNota.name)
      }
      select<Int>("Nota") {
        this.setWidthFull()
        this.isEmptySelectionAllowed = true
        setItems(ETipoNota.entries.map { it.num })
        this.setItemLabelGenerator {
          ETipoNota.entries.firstOrNull { et -> et.num == it }?.descricao ?: ""
        }
        binder.bind(this, UserSaci::tipoNota.name)
        value = ETipoNota.TODOS.num
      }
      multiSelectComboBox<String>("Localização") {
        this.setWidthFull()
        setItems(listOf("TODOS") + viewModel.allLocalizacao())
        binder.bind(this, UserSaci::localizacaoNota.name)
      }
    }
  }
}