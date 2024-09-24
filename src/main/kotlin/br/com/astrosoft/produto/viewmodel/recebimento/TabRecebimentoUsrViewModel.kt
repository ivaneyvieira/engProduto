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
    this.recebimentoReceber = usuario.recebimentoReceber
    this.recebimentoRecebido = usuario.recebimentoRecebido
    this.lojaRec = usuario.lojaRec
    this.localizacaoRec = usuario.localizacaoRec
    this.recebimentoCadastraValidade = usuario.recebimentoCadastraValidade
    this.recebimentoDevClientes = usuario.recebimentoDevClientes
    this.recebimentoTransferencia = usuario.recebimentoTransferencia
    this.recebimentoTransfReceb = usuario.recebimentoTransfReceb
    this.recebimentoDevCliRec = usuario.recebimentoDevCliRec
    this.recebimentoReclassifica = usuario.recebimentoReclassifica
    this.recebimentoReclassRec = usuario.recebimentoReclassRec
    this.recebimentoReceberNota = usuario.recebimentoReceberNota
  }
}

interface ITabRecebimentoUsr : ITabUser
