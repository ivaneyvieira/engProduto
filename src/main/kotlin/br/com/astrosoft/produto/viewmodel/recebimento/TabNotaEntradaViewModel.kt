package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.TextBuffer
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintNotaRecebimento
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class TabNotaEntradaViewModel(val viewModel: RecebimentoViewModel) {
  val subView
    get() = viewModel.view.tabNotaEntrada

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

  fun imprimeNotas() = viewModel.exec {
    val itens = subView.notasSelecionadas()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val report = PrintNotaRecebimento()
    val preview = subView.printerPreview(loja = 0)

    val buf = TextBuffer()


    itens.forEach { nota ->
      report.print(
        dados = nota.produtos,
        printer = object : IPrinter {
          override fun print(text: TextBuffer) {
            buf.println(text.textBuf())
          }
        }
      )
    }

    preview.print(buf)
  }

  fun proximoNumeroDevolucao(): Int {
    return saci.proximoNumeroDevolucao()
  }

  fun devolucaoProduto(produtos: List<NotaRecebimentoProduto>, tipo: ETipoDevolucao) = viewModel.exec {

    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    subView.dlgDevoucao(produtos, tipo) { numero: Int?, msg: String ->
      if (numero == null) {
        viewModel.view.showError(msg)
      } else {
        produtos.forEach { produto ->
          produto.tipoDevolucao = tipo.num
          produto.updateDevolucao(numero, tipo)
        }

        viewModel.view.showInformation("Criada registro de devolução: $numero com o motivo ${tipo.descricao}")

        subView.updateProduto()
      }
    }
  }

  fun desfazerDevolucao(produtos: List<NotaRecebimentoProduto>) = viewModel.exec {
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    produtos.forEach { produto ->
      produto.desfazerDevolucao()
    }
    subView.updateProduto()
  }

  fun findNota(numeroForm: Int): List<DadosDevolucao> {
    return DadosDevolucao.findNota(numeroForm)
  }
}

interface ITabNotaEntrada : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
  fun updateArquivos()
  fun arquivosSelecionados(): List<InvFile>
  fun produtosSelecionados(): List<NotaRecebimentoProduto>
  fun notasSelecionadas(): List<NotaRecebimento>
  fun updateProduto(): NotaRecebimento?
  fun dlgDevoucao(
    produtos: List<NotaRecebimentoProduto>,
    motivo: ETipoDevolucao,
    block: (numero: Int?, msg: String) -> Unit
  )
}

data class ResultDialog(
  val numero: Int? = null,
  val msg: String = "",
)