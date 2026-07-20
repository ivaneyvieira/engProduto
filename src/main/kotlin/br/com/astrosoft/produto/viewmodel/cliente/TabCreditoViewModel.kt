package br.com.astrosoft.produto.viewmodel.cliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.DadosCredito
import br.com.astrosoft.produto.model.beans.FiltroDadosCredito
import br.com.astrosoft.produto.model.planilha.PlanilhaDadosCredito

class TabCreditoViewModel(val viewModel: ClienteViewModel) {
  val subView
    get() = viewModel.view.tabCredito

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = DadosCredito.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun geraPlanilha(): ByteArray {
    val clientes = subView.clientesSelecionados()
    if (clientes.isEmpty()) {
      viewModel.view.showError("Selecione os clientes para gerar a planilha")
      return byteArrayOf()
    } else {
      val planilha = PlanilhaDadosCredito()
      return planilha.write(clientes)
    }
  }
}

interface ITabCredito : ITabView {
  fun filtro(): FiltroDadosCredito
  fun updateNotas(movManualList: List<DadosCredito>)
  fun clientesSelecionados(): List<DadosCredito>
}