package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroNotaEntradaXML
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaEntradaXML
import br.com.astrosoft.produto.model.beans.ProdutoNotaEntradaNdd

class TabRecebimentoPreEntViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabRecebimentoPreEnt

  val list = mutableListOf<NotaEntradaXML>()

  fun findLojas(): List<Loja> {
    return Loja.allLojas().sortedBy { it.no }
  }

  fun findNotas(filtro: FiltroNotaEntradaXML): List<NotaEntradaXML> {
    return NotaEntradaXML.findAll(filtro)
  }

  fun salvaNota(nota: NotaEntradaXML) {
    nota.save()
    updateViewBD()
  }

  fun updateViewBD() {
    val filter = subView.getFiltro()
    val listBD = NotaEntradaXML.findAll(filter).filter {
      filter.pedido == 0 || it.pedido == filter.pedido
    }
    list.clear()
    list.addAll(listBD)
    updateViewLocal()
  }

  fun updateViewLocal() {
    val query = subView.getFiltro().query
    val listLocal = list.filter { nota ->
      val cnpj = nota.cnpjEmitente
      val fornecedor = nota.nomeFornecedor
      val chave = nota.chave
      val valorProduto = nota.valorTotalProdutos.format().replace(".", "")
      val valorNota = nota.valorTotal.format().replace(".", "")
      val fornecedorCad = nota.fornecedorCad?.split(",").orEmpty()
      val fornecedorNota = nota.fornecedorNota?.toString() ?: ""
      query == "" || cnpj == query || fornecedor.contains(query, ignoreCase = true) ||
          chave.contains(query, ignoreCase = true) || valorProduto.startsWith(query) ||
          valorNota.startsWith(query) || fornecedorCad.contains(query) || fornecedorNota == query
    }

    subView.updateList(listLocal)
  }

  fun salvaItemPedido(ndd: ProdutoNotaEntradaNdd) {
    val pedido = ndd.pedidoXML
    pedido?.save()
    subView.updateDlgPedidos()
  }

  fun preEntrada() = viewModel.exec() {
    val itens = subView.itensSelecionados()
    itens.ifEmpty {
      fail("Nenhuma nota selecionada")
    }
    itens.forEach { nota ->
      if (nota.preEntrada == "S") {
        fail("Nota já processada")
      }
    }
    viewModel.view.showQuestion("Confirma a pré entrada das notas selecionadas?") {
      itens.forEach { nota ->
        nota.processaEntrada()
      }
      updateViewBD()
    }
  }
}

interface ITabRecebimentoPreEnt : ITabView {
  fun getFiltro(): FiltroNotaEntradaXML
  fun updateList(list: List<NotaEntradaXML>)
  fun updateDlgPedidos()
  fun itensSelecionados(): List<NotaEntradaXML>
}