package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaResumoCartao
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaResumoCartao
import br.com.astrosoft.produto.model.planilha.PlanilhaResumoCartao

class TabResumoCartaoViewModel(val viewModel: VendaRefViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaResumoCartao.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun geraPlanilha(vendas: List<NotaResumoCartao>): ByteArray {
    val planilha = PlanilhaResumoCartao()
    return planilha.write(vendas)
  }

  fun imprimeRelatorio() {
    //val notas = subView.itensNotasSelecionados()
    //val report = ReportResumo()
    //val file = report.processaRelatorio(notas)
    //viewModel.view.showReport(chave = "Vendas${System.nanoTime()}", report = file)
  }

  val subView
    get() = viewModel.view.tabResumoCartao
}

interface ITabResumoCartao : ITabView {
  fun filtro(): FiltroNotaResumoCartao
  fun updateNotas(notas: List<NotaResumoCartao>)
  fun itensNotasSelecionados(): List<NotaResumoCartao>
}