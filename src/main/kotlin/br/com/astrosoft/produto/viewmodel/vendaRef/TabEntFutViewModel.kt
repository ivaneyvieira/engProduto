package br.com.astrosoft.produto.viewmodel.vendaRef

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaVendasRef
import br.com.astrosoft.produto.model.report.ReportRomaneio
import br.com.astrosoft.produto.model.report.ReportVendaRef

class TabEntFutViewModel(val viewModel: VendaRefViewModel) {
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
    val notaGrupo = notas.groupBy {
      "${it.loja} ${it.pdv} ${it.transacao} ${it.nomeMetodo}}"
    }.mapNotNull {
      it.value.sortedByDescending { it.valorTipo }.firstOrNull()
    }.filter {
      it.tipo == filtro.tipoNFEntFut.codigo || filtro.tipoNFEntFut == ETipoNFEntFut.Todos
    }.filter {
      when (filtro.temEntrega) {
        ETemEntrega.SIM   -> it.nfEntrega.isNullOrBlank().not()
        ETemEntrega.NAO   -> it.nfEntrega.isNullOrBlank()
        ETemEntrega.TODOS -> true
      }
    }
    subView.updateNotas(notaGrupo)
  }
  
  fun geraPlanilha(vendas: List<NotaVendaRef>): ByteArray {
    val planilha = PlanilhaVendasRef()
    return planilha.write(vendas)
  }
  
  fun imprimeRomaneio() {
    val notas = subView.itensNotasSelecionados()
    val file = ReportRomaneio.processaRelatorio(notas)
    viewModel.view.showReport(chave = "Romaneio${System.nanoTime()}", report = file)
  }
  
  fun imprimeRelatorio() {
    val notas = subView.itensNotasSelecionados()
    val report = ReportVendaRef()
    val file = report.processaRelatorio(notas)
    viewModel.view.showReport(chave = "Vendas${System.nanoTime()}", report = file)
  }
  
  val subView
    get() = viewModel.view.tabEntFut
}

interface ITabEntFut : ITabView {
  fun filtro(): FiltroNotaVendaRef
  fun updateNotas(notas: List<NotaVendaRef>)
  fun itensNotasSelecionados(): List<NotaVendaRef>
}