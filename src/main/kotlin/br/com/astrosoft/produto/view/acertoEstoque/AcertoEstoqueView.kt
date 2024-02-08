package br.com.astrosoft.produto.view.acertoEstoque

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.acertoEstoque.AcertoEstoqueViewModel
import br.com.astrosoft.produto.viewmodel.acertoEstoque.IAcertoEstoqueView
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "acertoEstoque")
@PageTitle("Acerto Estoque")
@CssImport("./styles/gridTotal.css")
@PermitAll
class AcertoEstoqueView : ViewLayout<AcertoEstoqueViewModel>(), IAcertoEstoqueView {
  override val viewModel = AcertoEstoqueViewModel(this)
  override val tabAcertoEstoqueEntrada = TabAcertoEstoqueEntrada(viewModel.tabAcertoEstoqueEntradaViewModel)
  override val tabAcertoEstoqueSaida = TabAcertoEstoqueSaida(viewModel.tabAcertoEstoqueSaidaViewModel)
  override val tabAcertoMovManualSaida = TabAcertoMovManualSaida(viewModel.tabAcertoMovManualSaidaViewModel)
  override val tabAcertoMovManualEntrada = TabAcertoMovManualEntrada(viewModel.tabAcertoMovManualEntradaViewModel)
  override val tabAcertoMovAtacado = TabAcertoMovAtacado(viewModel.tabAcertoMovAtacadoViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.acertoEstoque
  }

  init {
    addTabSheat(viewModel)
  }
}