package br.com.astrosoft.produto.viewmodel.devFor

class TabAvariaRecEmailViewModel(viewModel: DevolucaoAvariaRecViewModel) :
  TabDevolucaoViewModelAbstract<IDevolucaoAvariaRecView>(viewModel) {
  override val subView
    get() = viewModel.view.tabAvariaRecEmail
}

interface ITabAvariaRecEmail : ITabNota {
  override val serie: Serie
    get() = Serie.AVA
  override val pago66: SimNao
    get() = SimNao.NONE
  override val pago01: SimNao
    get() = SimNao.NONE
  override val coleta01: SimNao
    get() = SimNao.NONE
  override val remessaConserto: SimNao
    get() = SimNao.NONE
}