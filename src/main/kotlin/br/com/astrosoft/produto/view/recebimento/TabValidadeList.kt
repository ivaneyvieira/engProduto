package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.beans.Validade
import br.com.astrosoft.produto.viewmodel.recebimento.ITabValidadeList
import br.com.astrosoft.produto.viewmodel.recebimento.TabValidadeListViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class sTabValidadeList(val viewModel: TabValidadeListViewModel) :
  TabPanelGrid<Validade>(Validade::class),
  ITabValidadeList {

  override fun HorizontalLayout.toolBarConfig() {
    button("Adicionar") {
      this.icon = VaadinIcon.PLUS.create()
      onClick {
        viewModel.addValidade()
      }
    }
  }

  override fun Grid<Validade>.gridPanel() {
    this.addClassName("styling")
    this.withEditor(Validade::class,
      openEditor = {
        this.focusEditor(Validade::validade)
      },
      closeEditor = {
        viewModel.salvaValidade(it.bean)
      })

    this.addColumnButton(VaadinIcon.TRASH, "Excluir", "Excluir") { bean ->
      viewModel.deleteValidade(bean)
    }

    columnGrid(Validade::validade, header = "Validade").integerFieldEditor()
    columnGrid(Validade::mesesFabricacao, header = "Meses de Fabricação").integerFieldEditor()
  }

  override fun updateValidades(validadeList: List<Validade>) {
    updateGrid(validadeList)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.admin == true
  }

  override val label: String
    get() = "Validade"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraProduto.orEmpty().toList()
  }
}