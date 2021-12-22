package br.com.astrosoft.framework.session

import br.com.astrosoft.framework.model.Config
import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.content
import com.github.mvysny.karibudsl.v10.loginForm
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.login.LoginI18n
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo

@PageTitle("Login")
@Route("login")
//@Theme(value = Lumo::class, variant = Lumo.DARK)
class LoginView : KComposite(), BeforeEnterObserver {

  override fun beforeEnter(event: BeforeEnterEvent) {
    if (SecurityUtils.isUserLoggedIn) {
      navigateToMain()
    }
  }

  private lateinit var loginForm: LoginForm
  private val root = ui {
    verticalLayout {
      setSizeFull(); isPadding = false
      content { center() }

      val loginI18n = loginI18n()
      loginForm = loginForm(loginI18n) {
        addLoginListener { e ->
          if (!SecurityUtils.login(e.username, e.password)) {
            isError = true
          }
          else {
            navigateToMain()
          }
        }
      }
    }
  }

  private fun navigateToMain() {
    navigateTo(Config.mainClass)
  }

  private fun loginI18n() = LoginI18n.createDefault().apply {
    this.form.username = "Usuário"
    this.form.title = Config.title
    this.form.submit = "Entrar"
    this.form.password = "Senha"
    this.form.forgotPassword = ""
    this.errorMessage.title = "Usuário/senha inválidos"
    this.errorMessage.message = "Confira seu usuário e senha e tente novamente."
    this.additionalInformation = "Versão ${Config.version}"
  }
}

@BodySize(width = "100vw", height = "100vh")
@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes")
@Theme(value = "myapp", variant = Lumo.DARK)
class app : AppShellConfigurator