package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.reposicao.IReposicaoView
import br.com.astrosoft.produto.viewmodel.reposicao.ReposicaoViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "repsicao")
@PageTitle("Reposição")
@CssImport("./styles/gridTotal.css")
@PermitAll
class ReposicaoView(val init: Boolean = true) : ViewLayout<ReposicaoViewModel>(), IReposicaoView {
  override val viewModel = ReposicaoViewModel(this)
  override val tabReposicaoSep = TabReposicaoSep(viewModel.tabReposicaoSepViewModel)
  override val tabReposicaoEnt = TabReposicaoEnt(viewModel.tabReposicaoEntViewModel)
  override val tabReposicaoUsr = TabReposicaoUsr(viewModel.tabReposicaoUsrViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.reposicao
  }

  init {
    if (init) {
      addTabSheat(viewModel)
    }
  }
}