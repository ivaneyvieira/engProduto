package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.ViewLayout
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.ProdutoLayout
import br.com.astrosoft.produto.viewmodel.produto.IProdutoView
import br.com.astrosoft.produto.viewmodel.produto.ProdutoViewModel
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll

@Route(layout = ProdutoLayout::class, value = "produto")
@PageTitle("Produto")
@CssImport("./styles/gridTotal.css")
@PermitAll
class ProdutoView : ViewLayout<ProdutoViewModel>(), IProdutoView {
  override val viewModel: ProdutoViewModel = ProdutoViewModel(this)

  override val tabProdutoList = TabProdutoList(viewModel.tabProdutoListViewModel)
  override val tabProdutoEstoque = TabProdutoEstoque(viewModel.tabProdutoEstoqueViewModel)
  override val tabProdutoCadastro = TabProdutoCadastro(viewModel.tabProdutoCadastroViewModel)
  override val tabProdutoSped = TabProdutoSped(viewModel.tabProdutoSpedViewModel)
  override val tabProdutoInventario = TabProdutoInventario(viewModel.tabProdutoInventarioViewModel)
  override val tabProdutoInventarioAgrupado =
      TabProdutoInventarioAgrupado(viewModel.tabProdutoInventarioAgrupadoViewModel)
  override val tabProdutoUsr = TabProdutoUsr(viewModel.tabProdutoUsrViewModel)
  override val tabDadosValidade = TabDadosValidade(viewModel.tabDadosValidadeViewModel)
  override val tabEstoqueGiroViewModel = TabEstoqueGiroProduto(viewModel.tabEstoqueGiroViewModel)
  override val tabEstoqueValidadeViewModel = TabEstoqueValidadeProduto(viewModel.tabEstoqueValidadeViewModel)
  override val tabEstoqueValidadeLojaViewModel = TabEstoqueValidadeLoja(viewModel.tabEstoqueValidadeLojaViewModel)

  override fun isAccept(): Boolean {
    val userSaci = AppConfig.userLogin() as? UserSaci ?: return false
    return userSaci.produto
  }

  init {
    addTabSheat(viewModel)
  }
}

