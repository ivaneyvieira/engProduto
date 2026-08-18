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
    this.devCliDevolucoes = usuario.devCliDevolucoes
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
    this.impressoraDev = usuario.impressoraDev
    this.lojaVale = usuario.lojaVale
    this.devCliAutoriza = usuario.devCliAutoriza
    this.devCliCancela = usuario.devCliCancela
    this.autorizaDev = usuario.autorizaDev
    this.desautorizaDev = usuario.desautorizaDev
    this.defazSolicitacao = usuario.defazSolicitacao
    this.liberaImpressao = usuario.liberaImpressao
    this.localizacaoDev = usuario.localizacaoDev
    this.devDados = usuario.devDados

    this.autorizaImpTrocaP = usuario.autorizaImpTrocaP
    this.autorizaImpTroca = usuario.autorizaImpTroca
    this.autorizaImpEstorno = usuario.autorizaImpEstorno
    this.autorizaImpReembolso = usuario.autorizaImpReembolso
    this.autorizaImpMuda = usuario.autorizaImpMuda

    this.devDadosProduto = usuario.devDadosProduto
  }
}

interface ITabDevCliUsr : ITabUser

fun UserSaci?.autorizaImp(): Boolean {
  this ?: return false

  val autorizaImpTrocaP = this.autorizaImpTrocaP
  val autorizaImpTroca = this.autorizaImpTroca
  val autorizaImpEstorno = this.autorizaImpEstorno
  val autorizaImpReembolso = this.autorizaImpReembolso
  val autorizaImpMuda = this.autorizaImpMuda

  return autorizaImpTrocaP || autorizaImpTroca || autorizaImpEstorno || autorizaImpReembolso || autorizaImpMuda
}
