package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.DadosEtiqueta
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
    updateView()
    val chaveExp = itens.chave()
    val split = chaveExp.split("_")
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.print(impressora, DadosEtiqueta(titulo = "Exp",
                                                      usuario = split.getOrNull(1) ?: "",
                                                      nota = split.getOrNull(2) ?: "",
                                                      data = split.getOrNull(3) ?: "",
                                                      hora = split.getOrNull(4) ?: "",
                                                      local = split.getOrNull(5) ?: ""))
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

private fun List<ProdutoNF>.chave(): String {
  val usuarioExp = this.firstOrNull()?.usuarioExp ?: return ""
  val nota = this.firstOrNull()?.nota ?: return ""
  val usuario = usuarioExp.split("_").getOrNull(0) ?: ""
  val data = usuarioExp.split("_").getOrNull(1) ?: ""
  val hora = usuarioExp.split("_").getOrNull(2) ?: ""
  val loc = this.firstOrNull()?.localizacao ?: return ""
  return usuario + "_" + nota + "_" + data + "_" + hora + "_" + loc
}

interface ITabNotaExp : ITabView {
  fun filtro(marca: EMarcaNota): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNF>
}