package br.com.astrosoft.produto.view.acertoEstoque

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.acertoEstoque.ITabAcertoUsr
import br.com.astrosoft.produto.viewmodel.acertoEstoque.TabAcertoUsrViewModel
import com.github.mvysny.karibudsl.v10.KFormLayout
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.data.binder.Binder

class TabAcertoUsr(viewModel: TabAcertoUsrViewModel) : TabPanelUser(viewModel), ITabAcertoUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::acertoPedido, "Pedido")
    columnGrid(UserSaci::acertoEntrada, "Entrada")
    columnGrid(UserSaci::acertoSaida, "Saída")
    columnGrid(UserSaci::acertoMovManualEntrada, "Ent Manual")
    columnGrid(UserSaci::acertoMovManualSaida, "Sai Manual")
    columnGrid(UserSaci::acertoMovAtacado, "Atacado")
  }

  override fun HorizontalLayout.configFields(binder: Binder<UserSaci>) {
    horizontalBlock {
      verticalBlock("Menu") {
        checkBox("Pedido") {
          binder.bind(this, UserSaci::acertoPedido.name)
        }
        checkBox("Entrada") {
          binder.bind(this, UserSaci::acertoEntrada.name)
        }
        checkBox("Saída") {
          binder.bind(this, UserSaci::acertoSaida.name)
        }
        checkBox("Ent Manual") {
          binder.bind(this, UserSaci::acertoMovManualEntrada.name)
        }
        checkBox("Sai Manual") {
          binder.bind(this, UserSaci::acertoMovManualSaida.name)
        }
        checkBox("Atacado") {
          binder.bind(this, UserSaci::acertoMovAtacado.name)
        }
      }
      verticalBlock("Comando") {
        checkBox("Remove Produto") {
          binder.bind(this, UserSaci::acertoRemoveProd.name)
        }
      }
    }
    verticalBlock("Filtros") {
      filtroLoja(binder, UserSaci::lojaAcerto)
      filtroImpressoraTermica(binder, UserSaci::impressoraAcerto)
    }
  }
}
