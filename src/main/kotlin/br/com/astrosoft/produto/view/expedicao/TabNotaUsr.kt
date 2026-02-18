package br.com.astrosoft.produto.view.expedicao

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.ETipoNotaFiscal
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.expedicao.ITabNotaUsr
import br.com.astrosoft.produto.viewmodel.expedicao.TabNotaUsrViewModel
import com.github.mvysny.karibudsl.v10.KFormLayout
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.binder.Binder

class TabNotaUsr(viewModel: TabNotaUsrViewModel) : TabPanelUser(viewModel), ITabNotaUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::notaTipo, "CD5A")
    columnGrid(UserSaci::notaSep, "Sep")
    columnGrid(UserSaci::notaRota, "Rota")
    columnGrid(UserSaci::notaTroca, "Troca")
    columnGrid(UserSaci::notaExp, "Exp")
    columnGrid(UserSaci::notaCD, "CD")
    columnGrid(UserSaci::notaEnt, "Entregue")
  }

  override fun HorizontalLayout.configFields(binder: Binder<UserSaci>) {
    verticalBlock("Menus") {
      checkBox("CD5A") {
        binder.bind(this, UserSaci::notaTipo.name)
      }
      checkBox("Sep CD5A") {
        binder.bind(this, UserSaci::notaSep.name)
      }
      checkBox("Rota CD5A") {
        binder.bind(this, UserSaci::notaRota.name)
      }
      checkBox("Troca") {
        binder.bind(this, UserSaci::notaTroca.name)
      }
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
      filtroImpressoraTermica(binder, UserSaci::impressoraNotaTermica)
      multiSelectComboBox<ETipoNotaFiscal>("Tipo NF") {
        this.setWidthFull()
        this.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL)
        setItems(ETipoNotaFiscal.entries)
        this.setItemLabelGenerator { it.descricao }
        binder.bind(this, UserSaci::tipoNotaExpedicao.name)
      }
      filtroLoja(binder, UserSaci::lojaLocExpedicao, "Loja Localização")
      filtroLocalizacao(binder, UserSaci::localizacaoNota)
      checkBox("Ressuprimento") {
        binder.bind(this, UserSaci::notaRessuprimento.name)
      }
    }
  }
}