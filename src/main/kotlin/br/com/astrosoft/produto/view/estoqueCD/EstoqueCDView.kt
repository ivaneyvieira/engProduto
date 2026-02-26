package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.estoqueCD.EstoqueCDViewModel
import br.com.astrosoft.produto.viewmodel.estoqueCD.IEstoqueCDView
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "estoqueCD")
@PageTitle("Controle Estoque")
@CssImport("./styles/gridTotal.css")
@PermitAll
class EstoqueCDView : ViewLayout<EstoqueCDViewModel>(), IEstoqueCDView {
  override val viewModel = EstoqueCDViewModel(this)
  override val tabEstoqueMov = TabEstoqueMov(viewModel.tabEstoqueMovViewModel)
  override val tabEstoqueAcerto = TabEstoqueAcerto(viewModel.tabEstoqueAcertoViewModel)
  override val tabEstoqueForn = TabEstoqueForn(viewModel.tabEstoqueFornViewModel)
  override val tabEstoqueAcertoSimples = TabEstoqueAcertoSimples(viewModel.tabEstoqueAcertoSimplesViewModel)
  override val tabEstoqueAcertoMobile = TabEstoqueAcertoMobile(viewModel.tabEstoqueAcertoMobileViewModel)
  override val tabControleCD = TabControleCD(viewModel.tabControleCDViewModel)
  override val tabControleLoja = TabControleLoja(viewModel.tabControleLojaViewModel)
  override val tabEstoqueSaldo = TabEstoqueSaldo(viewModel.tabEstoqueSaldoViewModel)
  override val tabEstoqueConf = TabEstoqueConf(viewModel.tabEstoqueConfViewModel)
  override val tabEstoqueInventario = TabEstoqueInventario(viewModel.tabEstoqueInventarioViewModel)
  override val tabEstoqueCad = TabEstoqueCad(viewModel.tabEstoqueCadViewModel)
  override val tabEstoqueCD1A = TabEstoqueCD1A(viewModel.tabEstoqueCD1AViewModel)
  override val tabEstoqueUsr = TabEstoqueUsr(viewModel.tabEstoqueUsrViewModel)
  override val tabValidadeList = TabValidadeList(viewModel.tabValidadeListViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.estoqueCD
  }

  init {
    addTabSheat(viewModel)
  }
}