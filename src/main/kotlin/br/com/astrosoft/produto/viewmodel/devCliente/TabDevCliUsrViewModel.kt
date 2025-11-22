package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabDevCliUsrViewModel(val viewModel: DevClienteViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabDevCliUsr

  override fun UserSaci.desative() {
    this.devCliente = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.devCliente
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.devCliEditor = usuario.devCliEditor
    this.devCliImprimir = usuario.devCliImprimir
    this.devCliImpresso = usuario.devCliImpresso
    this.devCliValeTrocaProduto = usuario.devCliValeTrocaProduto
    this.devCliCredito = usuario.devCliCredito
    this.devClienteTroca = usuario.devClienteTroca
    this.devCliVenda = usuario.devCliVenda
    this.autorizaTrocaP = usuario.autorizaTrocaP
    this.autorizaTroca = usuario.autorizaTroca
    this.autorizaEstorno = usuario.autorizaEstorno
    this.autorizaReembolso = usuario.autorizaReembolso
    this.autorizaMuda = usuario.autorizaMuda
    this.autorizaMista = usuario.autorizaMista
    this.impressoraDev = usuario.impressoraDev
    this.lojaVale = usuario.lojaVale
    this.ajustaMista = usuario.ajustaMista
    this.devCliAutoriza = usuario.devCliAutoriza
    this.autorizaSolicitacao = usuario.autorizaSolicitacao
    this.autorizaDev = usuario.autorizaDev
    this.desautorizaDev = usuario.desautorizaDev
  }
}

interface ITabDevCliUsr : ITabUser
