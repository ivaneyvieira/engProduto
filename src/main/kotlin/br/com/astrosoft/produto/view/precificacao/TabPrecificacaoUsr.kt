package br.com.astrosoft.produto.view.precificacao

import br.com.astrosoft.framework.view.FormUsuario
import br.com.astrosoft.framework.view.vaadin.TabPanelUser
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.precificacao.ITabPrecificacaoUsr
import br.com.astrosoft.produto.viewmodel.precificacao.TabPrecificacaoUsrViewModel
import com.github.mvysny.karibudsl.v10.checkBox
import com.vaadin.flow.component.grid.Grid

class TabPrecificacaoUsr(viewModel: TabPrecificacaoUsrViewModel) : TabPanelUser(viewModel), ITabPrecificacaoUsr {
  override fun Grid<UserSaci>.configGrid() {
    columnGrid(UserSaci::precificacaoPrecificacao, "Precificação")
    columnGrid(UserSaci::precificacaoEntrada, "Precificação Entrada")
    columnGrid(UserSaci::precificacaoSaida, "Precificação Saída")
    columnGrid(UserSaci::precificacaoEntradaMa, "Prec Ent MA")
  }

  override fun FormUsuario.configFields() {
    verticalBlock("Menus") {
      checkBox("Precificação") {
        binder.bind(this, UserSaci::precificacaoPrecificacao.name)
      }
      checkBox("Precificação Entrada") {
        binder.bind(this, UserSaci::precificacaoEntrada.name)
      }
      checkBox("Precificação Saída") {
        binder.bind(this, UserSaci::precificacaoSaida.name)
      }
      checkBox("Prec Ent MA") {
        binder.bind(this, UserSaci::precificacaoEntradaMa.name)
      }
    }
  }
}