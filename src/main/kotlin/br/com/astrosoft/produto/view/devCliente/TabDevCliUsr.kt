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
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.integerField
import com.vaadin.flow.component.grid.Grid

class TabDevCliUsr(viewModel: TabDevCliUsrViewModel) : TabPanelUser(viewModel), ITabDevCliUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::devCliEditor, "Editor")
    columnGrid(UserSaci::devCliAutoriza, "Editor")
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
        checkBox("Autoriza") {
          binder.bind(this, UserSaci::devCliAutoriza.name)
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
        checkBox("Autoriza Troca P") {
          binder.bind(this, UserSaci::autorizaTrocaP.name)
        }
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
        checkBox("Autoriza Mista") {
          binder.bind(this, UserSaci::autorizaMista.name)
        }
        checkBox("Ajusta Troca Mista") {
          binder.bind(this, UserSaci::ajustaMista.name)
        }
        checkBox("Solicita Dev") {
          binder.bind(this, UserSaci::autorizaSolicitacao.name)
        }
        checkBox("Autoriza Dev") {
          binder.bind(this, UserSaci::autorizaDev.name)
        }
        checkBox("Desautoriza Dev") {
          binder.bind(this, UserSaci::desautorizaDev.name)
        }
      }
    }
    verticalBlock("Filtros") {
      filtroImpressoraTermica(binder, UserSaci::impressoraDev)
      filtroLoja(binder, UserSaci::lojaVale)
      horizontalLayout {
        integerField("Valor Troca P") {
          binder.bind(this, UserSaci::valorMinimoTrocaP.name)
        }
        integerField("Valor Troca") {
          binder.bind(this, UserSaci::valorMinimoTroca.name)
        }
        integerField("Valor Estorno") {
          binder.bind(this, UserSaci::valorMinimoEstorno.name)
        }
      }
      horizontalLayout {
        integerField("Valor Reeembolso") {
          binder.bind(this, UserSaci::valorMinimoReembolso.name)
        }
        integerField("Valor Muda") {
          binder.bind(this, UserSaci::valorMinimoMuda.name)
        }
      }
    }
  }
}
