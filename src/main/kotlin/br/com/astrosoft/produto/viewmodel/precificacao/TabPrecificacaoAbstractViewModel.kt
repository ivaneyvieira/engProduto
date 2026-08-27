package br.com.astrosoft.produto.viewmodel.precificacao

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.*
import kotlin.math.absoluteValue

abstract class TabPrecificacaoAbstractViewModel(val viewModel: PrecificacaoViewModel) {
  abstract val subView: ITabPrecificacaoViewModel

  fun updateView() {
    val filtro = subView.filtro()
    val list = Precificacao.findAll(filtro)
    val listFiltrada = list.filter { precificacao ->
      val percentual = filtro.percentualImposto ?: return@filter true

      val valor = when (filtro.tipoImposto) {
        ETipoImposto.IPI       -> precificacao.ipi
        ETipoImposto.IRST      -> precificacao.retido
        ETipoImposto.CICMS     -> precificacao.icmsp
        ETipoImposto.FRETE     -> precificacao.frete
        ETipoImposto.PISCOFINS -> precificacao.pisCofins
      }

      valor.format() == percentual.format()
    }.filter { precificacao ->
      when (filtro.diferenca) {
        EDifImposto.TODOS -> true
        EDifImposto.IPI   -> precificacao.nfIpi.format() != precificacao.ipi.format()
        EDifImposto.CICMS -> precificacao.nfIcms?.absoluteValue.format() != precificacao.icmsp?.absoluteValue.format()
        EDifImposto.IRST  -> precificacao.nfIrst.format() != precificacao.retido.format()
        EDifImposto.FRETE -> precificacao.nfFrete.format() != precificacao.frete.format()
      }
    }
    subView.updateGrid(listFiltrada)
  }

  fun updatePrecificacao(bean: BeanForm) {
    val list = subView.listSelected()
    Precificacao.updateItens(list, bean)
    updateView()
  }
}

interface ITabPrecificacaoViewModel : ITabView {
  fun filtro(): FiltroPrecificacao
  fun updateGrid(itens: List<Precificacao>)
  fun listSelected(): List<Precificacao>
}