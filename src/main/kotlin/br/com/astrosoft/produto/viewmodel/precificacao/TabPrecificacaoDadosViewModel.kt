package br.com.astrosoft.produto.viewmodel.precificacao

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.DadosPrecificacao
import br.com.astrosoft.produto.model.beans.FiltroDadosPrecificacao
import br.com.astrosoft.produto.model.beans.FiltroValoresPrecificacao

class TabPrecificacaoDadosViewModel(val viewModel: PrecificacaoViewModel) {
  val subView
    get() = viewModel.view.tabPrecificacaoDadosViewModel

  fun updateView() {
    val filtro = subView.filtro()
    val filtroAtual = TabPrecificacaoDadosViewModel.filtro
    val list = if (filtroAtual == filtro) {
      list ?: emptyList()
    } else {
      TabPrecificacaoDadosViewModel.filtro = filtro
      list = DadosPrecificacao.findAll(filtro)
      list ?: emptyList()
    }
    subView.updateGrid(list.filtroValores())
  }

  private fun List<DadosPrecificacao>.filtroValores(): List<DadosPrecificacao> {
    val filtrovalores = subView.filtroValores()
    return this.filter { dados ->
      dados.filtro(filtrovalores)
    }
  }

  private fun DadosPrecificacao.filtro(filtro: FiltroValoresPrecificacao): Boolean {
    val valoresRef = this.valores(filtro.campo, filtro.lojaRef).ifEmpty {
      return false
    }
    val valoresLoja = this.valores(filtro.campo, filtro.loja).ifEmpty {
      return false
    }

    if (valoresLoja.size > 1 && valoresRef.size > 1) {
      return true
    }

    val valoresRefQuali = if (valoresRef.size > 1) {
      valoresRef.filter { it.loja !in valoresLoja.map { it.loja } }
    } else {
      valoresRef
    }

    val valoresLojaQuali = if (valoresLoja.size > 1) {
      valoresLoja.filter { it.loja !in valoresRef.map { it.loja } }
    } else {
      valoresLoja
    }

    val testeRet = valoresRefQuali.map { vref ->
      val teste = valoresLojaQuali.all { vloja ->
        filtro.operacao.execute(vref.valor, vloja.valor)
      }
      teste
    }

    return testeRet.all { it }
  }

  companion object {
    private var filtro: FiltroDadosPrecificacao? = null
    private var list: List<DadosPrecificacao>? = null
  }
}

interface ITabPrecificacaoDadosViewModel : ITabView {
  fun filtro(): FiltroDadosPrecificacao
  fun filtroValores(): FiltroValoresPrecificacao
  fun updateGrid(itens: List<DadosPrecificacao>)
}
