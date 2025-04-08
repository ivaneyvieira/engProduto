package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.util.firstDayOfMonth
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

class TabValidadeViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabValidade

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaRecebimento.findAll(filtro)
    subView.updateNota(notas)
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun selecionaProdutos(nota: NotaRecebimento, codigoBarra: String) = viewModel.exec {
    val produto = nota.produtosCodigoBarras(codigoBarra.trim()) ?: fail("Produto não encontrado")
    produto.validaProduto()
    produto.selecionado = true
    produto.salva()
    subView.reloadGrid()
    subView.focusCodigoBarra()
  }

  private fun NotaRecebimentoProduto.validaProduto() {
    if (this.validadeValida != "S") {
      fail("Validade não cadastrada")
    }
    if (this.localizacao.isNullOrBlank()) {
      fail("Localização do produto não informada")
    }
    val numVal = this.validade
    val validade = Validade.findValidade(numVal ?: 0)
    if (validade != null) {
      val dataRecebimento = this.data ?: fail("Data da nota não informada")
      this.vencimento ?: fail("Data de vencimento obrigatório")
      val dataFabricacao = this.fabricacao ?: fail("Data de fabricação obrigatório")
      val dataFabricacaoLimite =
          dataRecebimento.minusMonths(validade.mesesFabricacao.toLong() - 1.toLong()).firstDayOfMonth()
      if (dataFabricacao < dataFabricacaoLimite) {
        fail("Data de fabricação inferior a ${dataFabricacaoLimite.format("MM/yy")}")
      }
    }
  }

  fun salvaValidades(produto: NotaRecebimentoProduto?) {
    produto ?: fail("Produto não encontrado")
    produto.salvaVencimento()
    updateView()
  }
}

interface ITabValidade : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
  fun updateProduto(): NotaRecebimento?
  fun closeDialog()
  fun focusCodigoBarra()
  fun produtosSelecionados(): List<NotaRecebimentoProduto>
  fun openValidade(tipoValidade: Int, tempoValidade: Int, block: (ValidadeSaci) -> Unit)
  fun reloadGrid()
}