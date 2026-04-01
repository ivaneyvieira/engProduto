package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaResumo
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaResumo
import br.com.astrosoft.produto.model.planilha.PlanilhaResumo

class TabResumoViewModel(val viewModel: VendaRefViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaResumo.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun geraPlanilha(vendas: List<NotaResumo>): ByteArray {
    val planilha = PlanilhaResumo()
    return planilha.write(vendas)
  }

  fun imprimeRelatorio() {
    //val notas = subView.itensNotasSelecionados()
    //val report = ReportResumo()
    //val file = report.processaRelatorio(notas)
    //viewModel.view.showReport(chave = "Vendas${System.nanoTime()}", report = file)
  }

  val subView
    get() = viewModel.view.tabResumo
}

interface ITabResumo : ITabView {
  fun filtro(): FiltroNotaResumo
  fun updateNotas(notas: List<NotaResumo>)
  fun itensNotasSelecionados(): List<NotaResumo>
}