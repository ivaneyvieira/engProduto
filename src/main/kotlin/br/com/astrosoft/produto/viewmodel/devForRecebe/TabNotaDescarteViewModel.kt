package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaNotasPedidos
import br.com.astrosoft.produto.model.report.RelatorioEspelhoNota
import br.com.astrosoft.produto.model.report.RelatorioNotaDevolucao
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class TabNotaDescarteViewModel(val viewModel: DevFor2ViewModel) : ITabNotaViewModel {
  val subView
    get() = viewModel.view.tabNotaDescarte

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaRecebimentoDev.findAllDev(filtro = filtro, situacaoDev = EStituacaoDev.DESCARTE)
    subView.updateNota(notas)
  }

  fun saveNota(nota: NotaRecebimentoDev, updateGrid: Boolean = false) {
    nota.save()
    if (updateGrid) {
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
      invno = nota.niPrincipal,
      numero = nota.numeroDevolucao,
      tipoDevolucao = nota.motivoDevolucao,
      seq = null,
      date = LocalDate.now(),
      fileName = fileName,
      file = dados,
    )
    invFile.save()

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

  override fun findProdutos(codigo: String): List<PrdGrade> {
    return saci.findProdutoGrades(codigo)
  }

  override fun addProduto(produto: NotaRecebimentoProdutoDev?): Unit = viewModel.exec {
    produto ?: fail("Nenhum produto selecionado")
    produto.insertProduto()
    subView.updateProduto()
  }

  fun removeProduto() = viewModel.exec {
    val lista = subView.produtosSelecionados()
    if (lista.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    viewModel.view.showQuestion("Remover produtos selecionados?") {
      lista.forEach {
        it.deleteProduto()
      }
      subView.updateProduto()
    }
  }

  override fun updateProduto(produto: NotaRecebimentoProdutoDev, grade: String?, ni: Int?) {
    produto.updateProduto(grade, ni)
    subView.updateProduto()
  }

  fun imprimirEspelhoNota(nota: NotaRecebimentoDev) = viewModel.exec {
    val file = RelatorioEspelhoNota.processaRelatorio(listNota = listOf(nota))
    viewModel.view.showReport(chave = "Espelho Nota${System.nanoTime()}", report = file)
  }

  fun imprimirRelatorioCompleto(nota: NotaRecebimentoDev) = viewModel.exec {
    val file = RelatorioNotaDevolucao.processaRelatorio(listNota = listOf(nota), resumida = false)

    viewModel.view.showReport(chave = "Relatorio Completo${System.nanoTime()}", report = file)
  }

  fun imprimirRelatorioReduzido(nota: NotaRecebimentoDev) = viewModel.exec {
    val file = RelatorioNotaDevolucao.processaRelatorio(listNota = listOf(nota), resumida = true)

    viewModel.view.showReport(chave = "Relatorio Reduzido${System.nanoTime()}", report = file)
  }

  fun geraPlanilha(produtos: List<NotaRecebimentoProdutoDev>): ByteArray {
    val planilha = PlanilhaNotasPedidos()
    return planilha.write(produtos)
  }
}

interface ITabNotaDescarte : ITabView {
  fun filtro(): FiltroNotaRecebimentoProdutoDev
  fun updateNota(notas: List<NotaRecebimentoDev>)
  fun updateArquivos()
  fun arquivosSelecionados(): List<InvFileDev>
  fun produtosSelecionados(): List<NotaRecebimentoProdutoDev>
  fun notasSelecionadas(): List<NotaRecebimentoDev>
  fun updateProduto(): NotaRecebimentoDev?
}