package br.com.astrosoft.produto.viewmodel.devFor

class TabAvariaRecEditorViewModel(viewModel: DevolucaoAvariaRecViewModel) :
  TabDevolucaoViewModelAbstract<IDevolucaoAvariaRecView>(viewModel) {
  override val subView
    get() = viewModel.view.tabAvariaRecEditor
}

interface ITabAvariaRecEditor : ITabNota {
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