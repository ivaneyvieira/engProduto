package br.com.astrosoft.produto.viewmodel.devFor2

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class TabNotaPendenciaViewModel(val viewModel: DevFor2ViewModel) {
  val subView
    get() = viewModel.view.tabNotaPendencia

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaRecebimento.findAll(filtro = filtro, marcaDevolucao = true, situacaoDev = EStituacaoDev.PENDENTE)
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
      title = nota.tipoDevolucaoEnun?.name ?: "",
      date = LocalDate.now(),
      fileName = fileName,
      file = dados,
    )
    invFile.update()
    subView.updateArquivos()
  }

  fun removeArquivosSelecionado() {
    val selecionado = subView.arquivosSelecionados()
    selecionado.forEach {
      it.delete()
    }

    subView.updateArquivos()
  }

  fun saveNota(nota: NotaRecebimento) {
    nota.save(nota)
  }

  fun findTransportadora(carrno: Int?): Transportadora? {
    carrno ?: return null
    return saci.findTransportadora(carrno)
  }

  fun marcaNFD() = viewModel.exec {
    val itens = subView.notasSelecionadas()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    itens.forEach {
      it.marcaSituacao(EStituacaoDev.NFD)
    }
    updateView()
  }

  fun updateMotivo(tipoDevolucao: ETipoDevolucao?)= viewModel.exec {
    tipoDevolucao ?: return@exec
    val itens = subView.notasSelecionadas()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    itens.forEach {bean ->
      bean.produtos.forEach {
        it.tipoDevolucao = tipoDevolucao.num
        it.salvaMotivoDevolucao()
      }
    }
    updateView()
  }
}

interface ITabNotaPendencia : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
  fun updateArquivos()
  fun arquivosSelecionados(): List<InvFile>
  fun produtosSelecionados(): List<NotaRecebimentoProduto>
  fun notasSelecionadas(): List<NotaRecebimento>
  fun updateProduto(): NotaRecebimento?
}