package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaNotasPedidos
import br.com.astrosoft.produto.model.report.RelatorioEspelhoNota
import br.com.astrosoft.produto.model.report.RelatorioNotaDevolucao
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class TabNotaFornecedorViewModel(val viewModel: DevFor2ViewModel) {
  val subView
    get() = viewModel.view.tabNotaFornecedor

  fun updateView() {
    val filtro = subView.filtro()
    val notas = FornecedorClass.findAll(filtro = filtro)
    subView.updateFornecedor(notas)
  }
}

interface ITabNotaFornecedor : ITabView {
  fun filtro(): FiltroFornecedor
  fun updateFornecedor(fornecedore: List<FornecedorClass>)
}