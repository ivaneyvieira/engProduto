package br.com.astrosoft.produto.view

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.view.MainLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.notaEntrada.NotaEntradaView
import br.com.astrosoft.produto.view.notaSaida.NotaView
import br.com.astrosoft.produto.view.pedido.PedidoView
import br.com.astrosoft.produto.view.produto.ProdutoView
import br.com.astrosoft.produto.view.ressuprimento.RessuprimentoView
import com.vaadin.flow.component.icon.VaadinIcon.*
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo

class ProdutoLayout : MainLayout() {
  override fun Tabs.menuConfig() {
    val userSaci = Config.user as? UserSaci
    if (userSaci?.produto == true) menuRoute(FORM, "Produtos", ProdutoView::class)
    if (userSaci?.nota == true) menuRoute(DIPLOMA, "Notas de Saída", NotaView::class)
    if (userSaci?.notaEntrada == true) menuRoute(DIPLOMA, "Notas de Entrada", NotaEntradaView::class)
    if (userSaci?.pedido == true) menuRoute(DIPLOMA, "Pedido", PedidoView::class)
    if (userSaci?.pedido == true) menuRoute(DIPLOMA, "Ressuprimento", RessuprimentoView::class)
    if (userSaci?.admin == true) menuRoute(USER, "Usuário", UsuarioView::class, Config.isAdmin)
  }
}

@BodySize(width = "100vw", height = "100vh")
@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes")
@Theme(value = "myapp", variant = Lumo.DARK)
class App : AppShellConfigurator