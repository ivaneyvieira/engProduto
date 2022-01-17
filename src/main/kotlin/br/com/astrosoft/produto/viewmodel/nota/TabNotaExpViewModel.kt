package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.DadosEtiquetaNota
import br.com.astrosoft.produto.model.zpl.EtiquetaChave
import java.time.LocalDate
import java.time.LocalTime

class TabNotaExpViewModel(val viewModel: NotaViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaNota.TODOS)
    val notas = NotaSaida.find(filtro)
    subView.updateNotas(notas)
  }

  fun findGrade(prd: ProdutoNF?, block: (List<PrdGrade>) -> Unit) = viewModel.exec {
    prd ?: return@exec
    val list = prd.findGrades()
    block(list)
  }

  fun marcaCD() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaNota.CD.num
      val dataHora = LocalDate.now().format() + "_" + LocalTime.now().format()
      val usuario = Config.user?.login ?: ""
      produtoNF.usuarioExp = usuario + "_" + dataHora
      produtoNF.usuarioCD = ""
      produtoNF.salva()
    }
    subView.updateProdutos()
    imprimeEtiqueta()
  }

  private fun imprimeEtiqueta() {
    val notaExp = subView.findNota() ?: return
    val produto = notaExp.produtos(EMarcaNota.CD).firstOrNull()
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.print(impressora,
                            DadosEtiquetaNota(titulo = "Exp",
                                              usuario = produto?.usuarioNameCD ?: "",
                                              nota = produto?.nota ?: "",
                                              data = produto?.dataCD ?: "",
                                              hora = produto?.horaCD ?: "",
                                              local = produto?.localizacao ?: ""))
        viewModel.showInformation("Impressão realizada na impressora $impressora")
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  val subView
    get() = viewModel.view.tabNotaExp
}

interface ITabNotaExp : ITabView {
  fun filtro(marca: EMarcaNota): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun findNota(): NotaSaida?
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNF>
}