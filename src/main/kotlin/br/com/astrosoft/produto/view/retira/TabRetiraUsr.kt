package br.com.astrosoft.produto.view.retira

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.ETipoRetira
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.retira.ITabPedidoRetiraUsr
import br.com.astrosoft.produto.viewmodel.retira.PedidoRetiraUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant
import com.vaadin.flow.component.grid.Grid

class TabRetiraUsr(viewModel: PedidoRetiraUsrViewModel) : TabPanelUser(viewModel), ITabPedidoRetiraUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::retiraImprimir, "Imprimir")
    columnGrid(UserSaci::retiraImpresso, "Impresso")
  }

  override fun FormUsuario.configFields() {
    horizontalBlock {
      verticalBlock("Menu") {
        checkBox("Imprimir") {
          binder.bind(this, UserSaci::retiraImprimir.name)
        }
        checkBox("Impresso") {
          binder.bind(this, UserSaci::retiraImpresso.name)
        }
      }
    }
    verticalBlock("Filtros") {
      filtroLoja(binder, UserSaci::lojaRetira)
      filtroImpressoraTermica(binder, UserSaci::impressoraRet)
      multiSelectComboBox<ETipoRetira>("Tipo Retira") {
        this.setWidthFull()
        this.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL)
        setItems(ETipoRetira.entries)
        this.setItemLabelGenerator { it.descricao }
        binder.bind(this, UserSaci::retiraTipo.name)
      }
    }
  }
}
