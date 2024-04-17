package br.com.astrosoft.produto.view.notaSaida

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.ETipoNotaFiscal
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.notaSaida.ITabNotaUsr
import br.com.astrosoft.produto.viewmodel.notaSaida.TabNotaUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant
import com.vaadin.flow.component.grid.Grid

class TabNotaUsr(viewModel: TabNotaUsrViewModel) : TabPanelUser(viewModel), ITabNotaUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::notaExp, "Exp")
    columnGrid(UserSaci::notaCD, "CD")
    columnGrid(UserSaci::notaEnt, "Entregue")
  }

  override fun FormUsuario.configFields() {
    verticalBlock("Menus") {
      checkBox("Exp") {
        binder.bind(this, UserSaci::notaExp.name)
      }
      checkBox("CD") {
        binder.bind(this, UserSaci::notaCD.name)
      }
      checkBox("Entregue") {
        binder.bind(this, UserSaci::notaEnt.name)
      }
    }
    verticalBlock("Filtros") {
      filtroLoja(binder, UserSaci::lojaNota)
      filtroImpressoraEtiqueta(binder, UserSaci::impressoraNota)
      multiSelectComboBox<ETipoNotaFiscal>("Tipo NF") {
        this.setWidthFull()
        this.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL)
        setItems(ETipoNotaFiscal.entries)
        this.setItemLabelGenerator { it.descricao }
        binder.bind(this, UserSaci::tipoNotaExpedicao.name)
      }
      filtroLoja(binder, UserSaci::lojaLocExpedicao, "Loja Localização")
      filtroLocalizacao(binder, UserSaci::localizacaoNota)
    }
  }
}