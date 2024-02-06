package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaMovManual

class TabAcertoMovManualSaidaViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoMovManualSaida

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = MovManual.findMovManual(filtro)
    subView.updateNotas(notas)
  }

  fun geraPlanilha(mov: List<MovManual>): ByteArray {
    val planilha = PlanilhaMovManual()
    return planilha.write(mov)
  }

  fun estorno(movs: List<MovManual>) {
    movs.ifEmpty { fail("Não há notas selecionadas") }
    viewModel.view.showQuestion("Confirma o estorno das movimentações selecionadas?") {
      movs.forEach { it.estorno() }
      updateView()
    }
  }
}

interface ITabAcertoMovManualSaida : ITabView {
  fun filtro(): MovManualFilter
  fun updateNotas(movManualList: List<MovManual>)
}