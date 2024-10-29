package br.com.astrosoft.produto.viewmodel.produto

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabProdutoUsrViewModel(val viewModel: ProdutoViewModel) : TabUsrViewModel(viewModel) {
  override val subView
    get() = viewModel.view.tabProdutoUsr

  override fun UserSaci.desative() {
    this.produto = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.produto
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.produtoList = usuario.produtoList
    this.produtoCadastro = usuario.produtoCadastro
    this.produtoSped = usuario.produtoSped
    this.produtoEstoqueGiro = usuario.produtoEstoqueGiro
    this.produtoEstoqueValidade = usuario.produtoEstoqueValidade
    this.produtoInventario = usuario.produtoInventario
    this.produtoInventarioAgrupado = usuario.produtoInventarioAgrupado
    this.produtoEditor = usuario.produtoEditor
    this.lojaProduto = usuario.lojaProduto
    this.impressoraProduto = usuario.impressoraProduto
  }
}

interface ITabProdutoUsr : ITabUser
