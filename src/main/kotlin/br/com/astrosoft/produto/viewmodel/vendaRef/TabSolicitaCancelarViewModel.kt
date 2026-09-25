package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroSolicitaCancelar
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaSolicitaCancelar

class TabSolicitaCancelarViewModel(val viewModel: VendaRefViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }
  
  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }
  
  fun updateView() {
    val filtro = subView.filtro()
    val itens = NotaSolicitaCancelar.findAll(filtro)
    subView.updateNotas(itens)
  }
  
  fun geraPlanilha(vendas: List<NotaSolicitaCancelar>): ByteArray { //val planilha = PlanilhaVendasRef()
    //return planilha.write(vendas)
    TODO()
  }
  
  fun imprimeRelatorio() {
    TODO() //val notas = subView.itensNotasSelecionados()
    //val report = ReportVendaRef()
    //val file = report.processaRelatorio(notas)
    //viewModel.view.showReport(chave = "Vendas${System.nanoTime()}", report = file)
  }
  
  val subView
    get() = viewModel.view.tabSolicitaCancelar
}

interface ITabSolicitaCancelar : ITabView {
  fun filtro(): FiltroSolicitaCancelar
  fun updateNotas(list: List<NotaSolicitaCancelar>)
  fun itensNotasSelecionados(): List<NotaSolicitaCancelar>
}