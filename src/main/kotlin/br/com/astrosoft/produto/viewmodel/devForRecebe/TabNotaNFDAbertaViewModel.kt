package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.NotaExpedicaoDev
import br.com.astrosoft.produto.model.zpl.EtiquetaChave
import java.time.LocalDate

class TabNotaNFDAbertaViewModel(val viewModel: DevFor2ViewModel) {
  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaSaidaDev.findDevolucao(filtro)
    subView.updateNotas(notas)
  }

  fun findGrade(prd: ProdutoNFS?, block: (List<PrdGrade>) -> Unit) = viewModel.exec {
    prd ?: return@exec
    val list = prd.findGrades()
    block(list)
  }

  private fun imprimeEtiqueta(produtos: List<ProdutoNFS>) {
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressoraNota?.let { impressora ->
      try {
        EtiquetaChave.printPreviewExp(impressora, produtos, 1)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  fun imprimeProdutosNota(nota: NotaSaidaDev, itensSelecionados: List<NotaSaidaDevProduto>) = viewModel.exec {
    if (itensSelecionados.isEmpty())
      fail("Nenhum produto selecionado")
    if (nota.cancelada == "S")
      fail("Nota cancelada")
    val report = NotaExpedicaoDev(nota)
    report.print(
      dados = itensSelecionados,
      printer = subView.printerPreview(loja = nota.loja),
    )
  }

  fun autorizaProduto(listaPrd: List<ProdutoNFS>, login: String, senha: String): UserSaci? {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }

    if (user == null) {
      viewModel.view.showError("Usuário ou senha inválidos")
    } else {
      listaPrd.forEach { produto ->
        produto.usernoExp = user.no
        produto.salva()
      }
    }

    return user
  }

  fun saveObs(nota: NotaSaidaDev) = viewModel.exec {
    nota.saveObs()
  }

  fun addArquivo(nota: NotaSaidaDev, fileName: String, dados: ByteArray) {
    val notaFile = NotaSaidaDevFile(
      seq = 0,
      loja = nota.loja,
      pdvno = nota.pdvno,
      xano = nota.xano,
      date = LocalDate.now(),
      filename = fileName,
      file = dados
    )
    notaFile.save()
    subView.updateViewFile()
  }

  fun removeArquivosSelecionado() {
    val arquivoSelectionado = subView.arquivosSelecionados()
    arquivoSelectionado.forEach {file ->
      file.delete()
    }
    subView.updateViewFile()
  }

  val subView
    get() = viewModel.view.tabNotaNFDAberta
}

interface ITabNotaNFDAberta : ITabView {
  fun filtro(): FiltroNotaDev
  fun updateNotas(notas: List<NotaSaidaDev>)
  fun findNota(): NotaSaidaDev?
  fun updateProdutos()
  fun produtosSelcionados(): List<NotaSaidaDevProduto>
  fun arquivosSelecionados(): List<NotaSaidaDevFile>
  fun updateViewFile()
}