package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabEstoqueUsrViewModel(val viewModel: EstoqueCDViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabEstoqueUsr

  override fun UserSaci.desative() {
    this.estoqueCD = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.estoqueCD
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.estoqueMov = usuario.estoqueMov
    this.estoqueSaldo = usuario.estoqueSaldo
    this.estoqueCad = usuario.estoqueCad
    this.estoqueCD1A = usuario.estoqueCD1A
    this.listaEstoque = usuario.listaEstoque
    this.dataIncialKardec = usuario.dataIncialKardec
    this.impressoraEstoque = usuario.impressoraEstoque
    this.estoqueConf = usuario.estoqueConf
    this.estoqueAcerto = usuario.estoqueAcerto
    this.estoqueAcertoMobile = usuario.estoqueAcertoMobile
    this.estoqueGravaAcerto = usuario.estoqueGravaAcerto
    this.estoqueInventario = usuario.estoqueInventario
    this.lojaConferencia = usuario.lojaConferencia
  }
}

interface ITabEstoqueUsr : ITabUser
