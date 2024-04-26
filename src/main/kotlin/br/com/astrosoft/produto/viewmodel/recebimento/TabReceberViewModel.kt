package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*

class TabReceberViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabReceber

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
    val produto = nota.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.marcaEnum = EMarcaRecebimento.RECEBIDO
    produto.salva()
    val novaNota = subView.updateProduto()
    if (novaNota == null || nota.produtos.isEmpty()){
      subView.closeDialog()
    }
  }
}

interface ITabReceber : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
  fun updateProduto(): NotaRecebimento?
  fun closeDialog()
}