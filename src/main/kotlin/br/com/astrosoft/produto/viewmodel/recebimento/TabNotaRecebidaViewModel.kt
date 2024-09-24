package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.produto.model.beans.FiltroNotaRecebimentoProduto
import br.com.astrosoft.produto.model.beans.InvFile
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.NotaRecebimento
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
}

interface ITabNotaRecebida : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
  fun updateArquivos()
  fun arquivosSelecionados(): List<InvFile>
}