package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci

class TabRecebimentoUsrViewModel(val viewModel: RecebimentoViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabRecebimentoUsr

  override fun UserSaci.desative() {
    this.recebimento = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.recebimento
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.recebimentoPedido = usuario.recebimentoPedido
    this.recebimentoAgenda = usuario.recebimentoAgenda
    this.recebimentoXML = usuario.recebimentoXML
    this.recebimentoPreEnt = usuario.recebimentoPreEnt
    this.recebimentoNotaEntrada = usuario.recebimentoNotaEntrada
    this.recebimentoReceberNota = usuario.recebimentoReceberNota
    this.recebimentoValidade = usuario.recebimentoValidade
    this.recebimentoNotaRecebida = usuario.recebimentoNotaRecebida

    this.lojaRec = usuario.lojaRec
    this.localizacaoRec = usuario.localizacaoRec
    this.impressoraRec = usuario.impressoraRec
  }
}

interface ITabRecebimentoUsr : ITabUser
