package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaVendaDet
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaVendaDet
import br.com.astrosoft.produto.model.planilha.PlanilhaVendasDet
import br.com.astrosoft.produto.model.report.ReportVendaDet

class TabVendaDetViewModel(val viewModel: VendaRefViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaVendaDet.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun geraPlanilha(vendas: List<NotaVendaDet>): ByteArray {
    val planilha = PlanilhaVendasDet()
    return planilha.write(vendas)
  }

  fun imprimeRelatorio() {
    val notas = subView.itensNotasSelecionados()
    val report = ReportVendaDet()
    val file = report.processaRelatorio(notas)
    viewModel.view.showReport(chave = "Vendas${System.nanoTime()}", report = file)
  }

  val subView
    get() = viewModel.view.tabVendaDet
}

interface ITabVendaDet : ITabView {
  fun filtro(): FiltroNotaVendaDet
  fun updateNotas(notas: List<NotaVendaDet>)
  fun itensNotasSelecionados(): List<NotaVendaDet>
}