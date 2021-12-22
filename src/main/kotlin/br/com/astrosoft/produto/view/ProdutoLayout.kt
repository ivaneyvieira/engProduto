package br.com.astrosoft.produto.view

import br.com.astrosoft.produto.view.produto.ProdutoView
import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.view.MainLayout
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.icon.VaadinIcon.FORM
import com.vaadin.flow.component.icon.VaadinIcon.USER
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo

@Theme(value = Lumo::class, variant = Lumo.DARK)
@JsModule("./styles/shared-styles.js")
class ProdutoLayout : MainLayout() {
  override fun Tabs.menuConfig() {
    menuRoute(FORM, "Produto", ProdutoView::class)
    menuRoute(USER, "Usuário", UsuarioView::class, Config.isAdmin)
  }
}
