package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.MovManual
import br.com.astrosoft.produto.model.beans.MovManualFilter
import br.com.astrosoft.produto.model.planilha.PlanilhaMovManual

class TabAcertoMovManualEntradaViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoMovManualEntrada

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
}

interface ITabAcertoMovManualEntrada : ITabView {
  fun filtro(): MovManualFilter
  fun updateNotas(movManualList: List<MovManual>)
}