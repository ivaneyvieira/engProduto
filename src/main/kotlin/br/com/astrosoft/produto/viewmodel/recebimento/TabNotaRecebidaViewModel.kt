package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import java.time.LocalDate

class TabNotaRecebidaViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabNotaRecebida

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

  fun addArquivo(nota: NotaRecebimento, fileName: String, dados: ByteArray) {
    val invFile = InvFile(
      seq = null,
      invno = nota.ni,
      title = fileName,
      date = LocalDate.now(),
      fileName = fileName,
      file = dados,
    )
    invFile.update()
    updateView()
    subView.updateArquivos()
  }

  fun removeArquivosSelecionado() {
    val selecionado = subView.arquivosSelecionados()
    selecionado.forEach {
      it.delete()
    }
    updateView()
    subView.updateArquivos()
  }

  fun voltar() = viewModel.exec {
    val itens = subView.produtosSelecionados()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    itens.forEach {
      it.devolver()
    }
    subView.updateProduto()
  }
}

interface ITabNotaRecebida : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
  fun updateArquivos()
  fun arquivosSelecionados(): List<InvFile>
  fun produtosSelecionados(): List<NotaRecebimentoProduto>
  fun updateProduto(): NotaRecebimento?
}