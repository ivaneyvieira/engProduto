package br.com.astrosoft.produto.viewmodel.devFor2

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.saci
import com.vaadin.flow.component.ClickEvent
import java.time.LocalDate

class TabNotaAcertoViewModel(val viewModel: DevFor2ViewModel) {
  val subView
    get() = viewModel.view.tabNotaAcerto

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaRecebimentoDev.findAllDev(filtro = filtro, situacaoDev = EStituacaoDev.ACERTO)
    subView.updateNota(notas)
  }

  fun saveNota(nota: NotaRecebimentoDev, updateGrid: Boolean = false) {
    nota.save()
    if(updateGrid){
      updateView()
    }
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

  fun marcaSituacao(situacao: EStituacaoDev) = viewModel.exec {
    val itens = subView.notasSelecionadas()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    itens.forEach {
      it.marcaSituacao(situacao)
    }
    updateView()
  }
}

interface ITabNotaAcerto : ITabView {
  fun filtro(): FiltroNotaRecebimentoProdutoDev
  fun updateNota(notas: List<NotaRecebimentoDev>)
  fun updateArquivos()
  fun arquivosSelecionados(): List<InvFileDev>
  fun produtosSelecionados(): List<NotaRecebimentoProdutoDev>
  fun notasSelecionadas(): List<NotaRecebimentoDev>
  fun updateProduto(): NotaRecebimentoDev?
}