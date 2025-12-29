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
    this.estoqueLoja = usuario.estoqueLoja
    this.estoqueSaldo = usuario.estoqueSaldo
    this.estoqueCad = usuario.estoqueCad
    this.estoqueCD1A = usuario.estoqueCD1A
    this.listaEstoque = usuario.listaEstoque
    this.dataIncialKardec = usuario.dataIncialKardec
    this.impressoraEstoque = usuario.impressoraEstoque
    this.estoqueConf = usuario.estoqueConf
    this.estoqueAcerto = usuario.estoqueAcerto
    this.estoqueAcertoMobile = usuario.estoqueAcertoMobile
    this.estoqueAcertoSimples = usuario.estoqueAcertoSimples
    this.estoqueGravaAcerto = usuario.estoqueGravaAcerto
    this.estoqueInventario = usuario.estoqueInventario
    this.lojaConferencia = usuario.lojaConferencia
    this.estoqueGarantia = usuario.estoqueGarantia
    this.estoqueGravaGarantia = usuario.estoqueGravaGarantia
    this.estoqueInsereInventarioCD = usuario.estoqueInsereInventarioCD
    this.estoqueForn = usuario.estoqueForn
    this.estoqueEditaConferencia = usuario.estoqueEditaConferencia
    this.controleLoja = usuario.controleLoja
    this.estoqueInsereInventarioLoja = usuario.estoqueInsereInventarioLoja
    this.estoqueAlteraInventarioLoja = usuario.estoqueAlteraInventarioLoja
    this.estoqueAlteraInventarioCD = usuario.estoqueAlteraInventarioCD
  }
}

interface ITabEstoqueUsr : ITabUser
