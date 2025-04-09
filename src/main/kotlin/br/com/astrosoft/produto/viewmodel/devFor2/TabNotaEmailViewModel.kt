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
    val notas = NotaRecebimentoDev.findAllDev(filtro = filtro, situacaoDev = EStituacaoDev.EMAIL)
    subView.updateNota(notas)
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun addArquivo(nota: NotaRecebimentoDev, fileName: String, dados: ByteArray) {
    val invFile = InvFileDev(
      invno = nota.ni,
      numero = nota.numeroDevolucao,
      tipoDevolucao = nota.tipoDevolucao,
      seq = null,
      date = LocalDate.now(),
      fileName = fileName,
      file = dados,
    )
    invFile.update()

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
    subView.updateArquivos()
  }

  fun saveNota(nota: NotaRecebimentoDev, updateGrid: Boolean = false) {
    nota.save()
    if(updateGrid){
      updateView()
    }
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

  fun marcaReposto() = viewModel.exec {
    val itens = subView.notasSelecionadas()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    itens.forEach {
      it.marcaSituacao(EStituacaoDev.REPOSTO)
    }
    updateView()
  }
}

interface ITabNotaEmail : ITabView {
  fun filtro(): FiltroNotaRecebimentoProdutoDev
  fun updateNota(notas: List<NotaRecebimentoDev>)
  fun updateArquivos()
  fun arquivosSelecionados(): List<InvFileDev>
  fun produtosSelecionados(): List<NotaRecebimentoProdutoDev>
  fun notasSelecionadas(): List<NotaRecebimentoDev>
  fun updateProduto(): NotaRecebimentoDev?
}