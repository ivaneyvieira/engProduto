package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevCliUsr
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCliUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabDevCliUsr(viewModel: TabDevCliUsrViewModel) : TabPanelUser(viewModel), ITabDevCliUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::devCliEditor, "Editor")
    columnGrid(UserSaci::devCliImprimir, "VC Imprimir")
    columnGrid(UserSaci::devCliImpresso, "VC Impresso")
    columnGrid(UserSaci::devCliValeTrocaProduto, "Produto")
    columnGrid(UserSaci::devCliCredito, "Crédito")
    columnGrid(UserSaci::devClienteTroca, "Troca")
    columnGrid(UserSaci::devCliVenda, "Venda")
  }

  override fun FormUsuario.configFields() {
    horizontalBlock {
      verticalBlock("Menu") {
        checkBox("Editor") {
          binder.bind(this, UserSaci::devCliEditor.name)
        }
        checkBox("VC Imprimir") {
          binder.bind(this, UserSaci::devCliImprimir.name)
        }
        checkBox("VC Impresso") {
          binder.bind(this, UserSaci::devCliImpresso.name)
        }
        checkBox("Produto") {
          binder.bind(this, UserSaci::devCliValeTrocaProduto.name)
        }
        checkBox("Crédito") {
          binder.bind(this, UserSaci::devCliCredito.name)
        }
        checkBox("Troca") {
          binder.bind(this, UserSaci::devClienteTroca.name)
        }
        checkBox("Venda") {
          binder.bind(this, UserSaci::devCliVenda.name)
        }
      }
      verticalBlock("Comandos") {
        checkBox("Autoriza Troca") {
          binder.bind(this, UserSaci::autorizaTroca.name)
        }
        checkBox("Autoriza Estorno") {
          binder.bind(this, UserSaci::autorizaEstorno.name)
        }
        checkBox("Autoriza Reembolso") {
          binder.bind(this, UserSaci::autorizaReembolso.name)
        }
        checkBox("Autoriza Muda") {
          binder.bind(this, UserSaci::autorizaMuda.name)
        }
      }
    }
    verticalBlock("Filtros") {
      filtroImpressoraTermica(binder, UserSaci::impressoraDev)
      filtroLoja(binder, UserSaci::lojaVale)
    }
  }
}
