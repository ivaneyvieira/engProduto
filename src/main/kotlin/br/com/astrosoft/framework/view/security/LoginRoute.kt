package br.com.astrosoft.framework.view.security

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.security.LoginService
import br.com.astrosoft.framework.view.config.ViewUtil
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.login.AbstractLogin.LoginEvent
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.login.LoginI18n
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.theme.lumo.LumoUtility
import javax.security.auth.login.LoginException

@Route("login")
@PageTitle("Login")
@AnonymousAllowed
class LoginRoute : VerticalLayout(), ComponentEventListener<LoginEvent> {
  private val login = LoginForm()

  init {
    setSizeFull()
    justifyContentMode = JustifyContentMode.CENTER
    alignItems = FlexComponent.Alignment.CENTER
    login.addLoginListener(this)
    login.setI18n(loginI18n())
    login.isForgotPasswordButtonVisible = false
    login.addClassNames(
      LumoUtility.Background.BASE,
      LumoUtility.BoxShadow.XLARGE,
      LumoUtility.Border.ALL,
      LumoUtility.BorderColor.CONTRAST_50,
      LumoUtility.BorderRadius.MEDIUM
    )
    add(login)
  }

  override fun onComponentEvent(loginEvent: LoginEvent) {
    try {
      LoginService.get().login(loginEvent.username, loginEvent.password)
    } catch (ex: LoginException) {
      println("Login failed")
      login.isError = true
    }
  }

  private fun loginI18n() = LoginI18n.createDefault().apply {
    this.form.username = "Usuário"
    this.form.title = AppConfig.title
    this.form.submit = "Entrar"
    this.form.password = "Senha"
    this.form.forgotPassword = ""
    this.errorMessage.title = "Usuário/senha inválidos"
    this.errorMessage.message = "Confira seu usuário e senha e tente novamente."
    this.additionalInformation = "Versão " + ViewUtil.versao
  }
}