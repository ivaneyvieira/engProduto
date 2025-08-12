package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabNotaUsrViewModel(val viewModel: DevFor2ViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabNotaUsr

  override fun UserSaci.desative() {
    this.devFor2 = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.devFor2
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.devFor2NotaEditor = usuario.devFor2NotaEditor
    this.devFor2NotaDivergente = usuario.devFor2NotaDivergente
    this.devFor2NotaPedido = usuario.devFor2NotaPedido
    this.devFor2NotaColeta = usuario.devFor2NotaColeta
    this.devFor2NotaNFD = usuario.devFor2NotaNFD
    this.devFor2NotaTransportadora = usuario.devFor2NotaTransportadora
    this.devFor2NotaEmail = usuario.devFor2NotaEmail
    this.devFor2NotaReposto = usuario.devFor2NotaReposto
    this.devFor2NotaAcerto = usuario.devFor2NotaAcerto
    this.devFor2NotaAcertoPago = usuario.devFor2NotaAcertoPago
    this.devFor2NotaAjuste = usuario.devFor2NotaAjuste
    this.devFor2NotaDescarte = usuario.devFor2NotaDescarte
    this.devFor2NotaNulo = usuario.devFor2NotaNulo
    this.devFor2NotaGarantia = usuario.devFor2NotaGarantia
    this.recebimentoNotaEntrada = usuario.recebimentoNotaEntrada
    this.notaNFDAberta = usuario.notaNFDAberta
    this.devFor2ImpressoraTermica = usuario.devFor2ImpressoraTermica
    this.devFor2NotaFornecedor = usuario.devFor2NotaFornecedor
    this.devFor2NotaRetornoNFD = usuario.devFor2NotaRetornoNFD
  }
}

interface ITabNotaUsr : ITabUser
