package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaResumoPgto
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaResumoPgto

class TabResumoPgtoViewModel(val viewModel: VendaRefViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaResumoPgto.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun geraPlanilha(vendas: List<NotaResumoPgto>): ByteArray {
    //val planilha = PlanilhaVendasRef()
    //return planilha.write(vendas)
    TODO()
  }

  fun imprimeRelatorio() {
    //val notas = subView.itensNotasSelecionados()
    //val report = ReportResumo()
    //val file = report.processaRelatorio(notas)
    //viewModel.view.showReport(chave = "Vendas${System.nanoTime()}", report = file)
  }

  val subView
    get() = viewModel.view.tabResumoPgto
}

interface ITabResumoPgto : ITabView {
  fun filtro(): FiltroNotaResumoPgto
  fun updateNotas(notas: List<NotaResumoPgto>)
  fun itensNotasSelecionados(): List<NotaResumoPgto>
}