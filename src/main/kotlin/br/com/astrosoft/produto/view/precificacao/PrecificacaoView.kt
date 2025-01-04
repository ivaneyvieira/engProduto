package br.com.astrosoft.produto.view.precificacao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.precificacao.IPrecificacaoView
import br.com.astrosoft.produto.viewmodel.precificacao.PrecificacaoViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "precificacao")
@PageTitle("Precificação")
@CssImport("./styles/gridTotal.css")
@PermitAll
class PrecificacaoView : ViewLayout<PrecificacaoViewModel>(), IPrecificacaoView {
  override val viewModel = PrecificacaoViewModel(this)
  override val tabPrecificacaoViewModel = TabPrecificacao(viewModel.tabPrecificacaoViewModel)
  override val tabPrecificacaoEntradaViewModel = TabPrecificacaoEntrada(viewModel.tabPrecificacaoEntradaViewModel)
  override val tabPrecificacaoEntradaMaViewModel = TabPrecificacaoEntradaMa(viewModel.tabPrecificacaoEntradaMaViewModel)
  override val tabPrecificacaoSaidaViewModel = TabPrecificacaoSaida(viewModel.tabPrecificacaoSaidaViewModel)
  override val tabPrecificacaoUsr = TabPrecificacaoUsr(viewModel.tabPrecificacaoUsrViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.precificacao
  }

  init {
    addTabSheat(viewModel)
  }
}