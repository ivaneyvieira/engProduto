package br.com.astrosoft.produto.viewmodel.cliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.DadosCliente
import br.com.astrosoft.produto.model.beans.FiltroDadosCliente
import br.com.astrosoft.produto.model.planilha.PlanilhaDadosCliente

class TabCadastroViewModel(val viewModel: ClienteViewModel) {
  val subView
    get() = viewModel.view.tabCadastro

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = DadosCliente.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun geraPlanilha(): ByteArray {
    val clientes = subView.clientesSelecionados()
    if (clientes.isEmpty()) {
      viewModel.view.showError("Selecione os clientes para gerar a planilha")
      return byteArrayOf()
    } else {
      val planilha = PlanilhaDadosCliente()
      return planilha.write(clientes)
    }
  }
}

interface ITabCadastro : ITabView {
  fun filtro(): FiltroDadosCliente
  fun updateNotas(movManualList: List<DadosCliente>)
  fun clientesSelecionados(): List<DadosCliente>
}