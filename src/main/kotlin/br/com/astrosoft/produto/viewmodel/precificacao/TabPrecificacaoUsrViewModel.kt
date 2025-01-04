package br.com.astrosoft.produto.viewmodel.precificacao

import br.com.astrosoft.framework.viewmodel.ITabUser
import br.com.astrosoft.framework.viewmodel.TabUsrViewModel
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.pedidoTransf.PedidoTransfViewModel

class TabPrecificacaoUsrViewModel(val viewModel: PrecificacaoViewModel) : TabUsrViewModel(viewModel) {

  override val subView
    get() = viewModel.view.tabPrecificacaoUsr

  override fun UserSaci.desative() {
    this.precificacao = false
  }

  override fun UserSaci.isActive(): Boolean {
    return this.precificacao
  }

  override fun UserSaci.update(usuario: UserSaci) {
    this.precificacaoPrecificacao = usuario.precificacaoPrecificacao
    this.precificacaoEntrada = usuario.precificacaoEntrada
    this.precificacaoSaida = usuario.precificacaoSaida
    this.precificacaoEntradaMa = usuario.precificacaoEntradaMa
  }
}

interface ITabPrecificacaoUsr : ITabUser
