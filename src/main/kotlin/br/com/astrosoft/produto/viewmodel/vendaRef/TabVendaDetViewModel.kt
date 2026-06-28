package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaVendaRef
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaVendaRef
import br.com.astrosoft.produto.model.planilha.PlanilhaVendasRef
import br.com.astrosoft.produto.model.report.ReportVendaRef

class TabVendaRefViewModel(val viewModel: VendaRefViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaVendaRef.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun geraPlanilha(vendas: List<NotaVendaRef>): ByteArray {
    val planilha = PlanilhaVendasRef()
    return planilha.write(vendas)
  }

  fun imprimeRelatorio() {
    val notas = subView.itensNotasSelecionados()
    val report = ReportVendaRef()
    val file = report.processaRelatorio(notas)
    viewModel.view.showReport(chave = "Vendas${System.nanoTime()}", report = file)
  }

  val subView
    get() = viewModel.view.tabVendaRef
}

interface ITabVendaRef : ITabView {
  fun filtro(): FiltroNotaVendaRef
  fun updateNotas(notas: List<NotaVendaRef>)
  fun itensNotasSelecionados(): List<NotaVendaRef>
}