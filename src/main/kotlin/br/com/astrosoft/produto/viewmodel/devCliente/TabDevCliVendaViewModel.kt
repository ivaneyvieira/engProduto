package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.view.vaadin.vaadin
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroNotaVenda
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaVenda
import br.com.astrosoft.produto.model.planilha.PlanilhaVendas
import br.com.astrosoft.produto.model.report.ReportVenda
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class TabDevCliVendaViewModel(val viewModel: DevClienteViewModel) : CoroutineScope {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaVenda.findAll(filtro)
    subView.updateNotas(notas)
  }

  fun geraPlanilha(vendas: List<NotaVenda>): ByteArray {
    val planilha = PlanilhaVendas()
    return planilha.write(vendas)
  }

  fun imprimeRelatorio() {
    val notas = subView.itensNotasSelecionados()
    val report = ReportVenda()
    val file = report.processaRelatorio(notas)
    viewModel.view.showReport(chave = "Vendas${System.nanoTime()}", report = file)
  }

  fun autorizaTroca() = viewModel.exec {
    val notas = subView.itensNotasSelecionados()
    if (notas.isEmpty()) {
      fail("Nenhuma nota selecionada")
    }

    notas.forEach {
      it.autoriza = "S"
      it.update()
    }
    updateView()
  }

  val subView
    get() = viewModel.view.tabDevCliVenda

  private val uiCoroutineScope = SupervisorJob()
  private val uiCoroutineContext = vaadin()
  override val coroutineContext: CoroutineContext
    get() = uiCoroutineContext + uiCoroutineScope
}

interface ITabDevVenda : ITabView {
  fun filtro(): FiltroNotaVenda
  fun updateNotas(notas: List<NotaVenda>)
  fun itensNotasSelecionados(): List<NotaVenda>
}