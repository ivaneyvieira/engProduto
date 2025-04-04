package br.com.astrosoft.produto.viewmodel.devFor2

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class TabNotaEmailViewModel(val viewModel: DevFor2ViewModel) {
  val subView
    get() = viewModel.view.tabNotaEmail

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaRecebimento.findAll(filtro = filtro, marcaDevolucao = true, situacaoDev = EStituacaoDev.EMAIL)
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

  fun findTransportadora(carrno: Int?): Transportadora? {
    carrno ?: return null
    return saci.findTransportadora(carrno)
  }

  fun removeArquivosSelecionado() {
    val selecionado = subView.arquivosSelecionados()
    selecionado.forEach {
      it.delete()
    }
    updateView()
    subView.updateArquivos()
  }

  fun marcaTransportadora() = viewModel.exec {
    val itens = subView.notasSelecionadas()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    itens.forEach {
      it.marcaSituacao(EStituacaoDev.TRANSPORTADORA)
    }
    updateView()
  }
}

interface ITabNotaEmail : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
  fun updateArquivos()
  fun arquivosSelecionados(): List<InvFile>
  fun produtosSelecionados(): List<NotaRecebimentoProduto>
  fun notasSelecionadas(): List<NotaRecebimento>
  fun updateProduto(): NotaRecebimento?
}