package br.com.astrosoft.produto.viewmodel.devFor2

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class TabNotaRepostoViewModel(val viewModel: DevFor2ViewModel) : ITabNotaViewModel {
  val subView
    get() = viewModel.view.tabNotaReposto

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaRecebimentoDev.findAllDev(filtro = filtro, situacaoDev = EStituacaoDev.REPOSTO)
    subView.updateNota(notas)
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun saveNota(nota: NotaRecebimentoDev, updateGrid: Boolean = false) {
    nota.save()
    if (updateGrid) {
      updateView()
    }
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun addArquivo(nota: NotaRecebimentoDev, fileName: String, dados: ByteArray) {
    val invFile = InvFileDev(
      invno = nota.niPrincipal,
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

  override fun findProdutos(codigo: String, loja: Int): List<PrdGrade> {
    return saci.findGrades(codigo, loja)
  }

  override fun addProduto(produto: NotaRecebimentoProdutoDev?): Unit = viewModel.exec {
    produto ?: fail("Nenhum produto selecionado")
    produto.saveProduto()
    subView.updateProduto()
  }
}

interface ITabNotaReposto : ITabView {
  fun filtro(): FiltroNotaRecebimentoProdutoDev
  fun updateNota(notas: List<NotaRecebimentoDev>)
  fun updateArquivos()
  fun arquivosSelecionados(): List<InvFileDev>
  fun produtosSelecionados(): List<NotaRecebimentoProdutoDev>
  fun notasSelecionadas(): List<NotaRecebimentoDev>
  fun updateProduto(): NotaRecebimentoDev?
}