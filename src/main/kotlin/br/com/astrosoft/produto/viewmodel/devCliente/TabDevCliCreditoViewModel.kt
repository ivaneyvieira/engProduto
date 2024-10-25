package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.CreditoCliente
import br.com.astrosoft.produto.model.beans.FiltroCreditoCliente
import br.com.astrosoft.produto.model.beans.Impressora
import br.com.astrosoft.produto.model.planilha.PlanilhaCredito
import br.com.astrosoft.produto.model.printText.CreditoDevolucao
import br.com.astrosoft.produto.model.printText.ValeTrocaDevolucao

class TabDevCliCreditoViewModel(val viewModel: DevClienteViewModel) {
  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val clientes = CreditoCliente.findCreditoCliente(filtro)
    subView.updateClientes(clientes)
  }

  fun geraPlanilha(clientes: List<CreditoCliente>): ByteArray {
    val planilha = PlanilhaCredito()
    return planilha.write(clientes)
  }

  fun imprimeCredito(nota: CreditoCliente) {
    val relatorio = CreditoDevolucao(nota)
    relatorio.print(nota.produtos(), subView.printerPreview(showPrinter = AppConfig.isAdmin, loja = 0) { impressora ->
      updateView()
    })
  }

  val subView
    get() = viewModel.view.tabDevCliCredito
}

interface ITabDevCliCredito : ITabView {
  fun filtro(): FiltroCreditoCliente
  fun updateClientes(clientes: List<CreditoCliente>)
}