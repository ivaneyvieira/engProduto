package br.com.astrosoft.produto.view.devFor

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import com.vaadin.flow.component.html.IFrame
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteParameters
import com.vaadin.flow.theme.lumo.LumoUtility
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "devFor")
@PageTitle("Dev For")
@PermitAll
class DevForView : IFrame() {
  init {
    setSizeFull()
    this.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.NONE)
    this.style.set("border-style", "none")
    //this.style.set("height", "0")
    this.style.set("overflow", "hidden")
    this.style.set("scrolling", "auto")
    val userSaci = AppConfig.userLogin() as? UserSaci
    val login = userSaci?.login ?: ""
    val password = userSaci?.senha ?: ""
    src = "$urlLocal/$login/$password"
  }

  companion object {
    private val urlLocal = "http://localhost:8080/Gradle___devolucao___devFornecedor_1_0_war/avariarec"
    private val url = "http://172.20.47.2:8020/devFornecedor/avariarec"
  }
}