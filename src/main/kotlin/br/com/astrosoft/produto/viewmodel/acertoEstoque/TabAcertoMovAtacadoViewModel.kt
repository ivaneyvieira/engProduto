package br.com.astrosoft.produto.viewmodel.acertoEstoque

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaMovManual

class TabAcertoMovAtacadoViewModel(val viewModel: AcertoEstoqueViewModel) {
  val subView
    get() = viewModel.view.tabAcertoMovAtacado

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val notas = MovAtacado.findMovAtacado(filtro)
    subView.updateNotas(notas)
  }

  fun geraPlanilha(mov: List<MovAtacado>): ByteArray {
    //val planilha = PlanilhaMovManual()
    //return planilha.write(mov)
    TODO()
  }
}

interface ITabAcertoMovAtacado : ITabView {
  fun filtro(): MovManualFilter
  fun updateNotas(movManualList: List<MovAtacado>)
}