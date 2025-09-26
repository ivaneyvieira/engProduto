package br.com.astrosoft.produto.viewmodel.recebimento

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.TextBuffer
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintNotaDoc
import br.com.astrosoft.produto.model.printText.PrintNotaRecebimento
import br.com.astrosoft.produto.model.saci
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

  fun assinaEnvio() = viewModel.exec {
    val itens = subView.notasSelecionadas()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    subView.formAssinaEnvio(itens)
  }

  fun assinaRecebe() = viewModel.exec {
    val itens = subView.notasSelecionadas()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    subView.formAssinaRecebe(itens)
  }

  fun assinaEnvio(itens: List<NotaRecebimento>, login: String, senha: String) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.equals(login, ignoreCase = true) && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    if (user.senha != senha) {
      fail("Senha inválida")
    }

    if (!user.ressuprimentoEnvioDoc) {
      fail("Usuário sem permissão para Assinar Envio")
    }

    val protocolo = saci.proximoNumero("PROTNOTA").numero?.toString() ?: "0"

    itens.forEach { nota ->
      nota.usernoEnvio = user.no
      nota.protocolo = protocolo
      nota.save()
    }
    updateView()
  }

  fun assinaRecebe(itens: List<NotaRecebimento>, login: String, senha: String) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.equals(login, ignoreCase = true) && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    if (user.senha != senha) {
      fail("Senha inválida")
    }

    if (!user.ressuprimentoRecebeDoc) {
      fail("Usuário sem permissão para Assinar Recebimento")
    }

    itens.forEach { nota ->
      nota.usernoReceb = user.no
      nota.save()
    }
    updateView()
  }

  fun imprimeListaDoc() = viewModel.exec {
    val itens = subView.notasSelecionadas()
    if (itens.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val relatorio = PrintNotaDoc()

    relatorio.print(
      dados = itens.sortedWith(
        compareBy(
          NotaRecebimento::loja,
          NotaRecebimento::data,
          NotaRecebimento::nfEntrada
        )
      ),
      printer = subView.printerPreview()
    )
  }
}

interface ITabNotaRecebida : ITabView {
  fun filtro(): FiltroNotaRecebimentoProduto
  fun updateNota(notas: List<NotaRecebimento>)
  fun updateArquivos()
  fun arquivosSelecionados(): List<InvFile>
  fun produtosSelecionados(): List<NotaRecebimentoProduto>
  fun notasSelecionadas(): List<NotaRecebimento>
  fun updateProduto(): NotaRecebimento?
  fun formAssinaEnvio(itens: List<NotaRecebimento>)
  fun formAssinaRecebe(itens: List<NotaRecebimento>)
}