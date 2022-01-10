package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

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
      produtoNF.salva()
    }
    subView.updateProdutos()
    val chave = itens.chave()
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      EtiquetaChave.print(impressora, chave)
    }

  }

  val subView
    get() = viewModel.view.tabNotaExp
}

private fun List<ProdutoNF>.chave(): String {
  val usuario = this.firstOrNull()?.usuario ?: return ""
  val nota = this.firstOrNull()?.nota ?: return ""
  val dataHora = this.firstOrNull()?.data_hora ?: return ""
  val loc = this.firstOrNull()?.localizacao ?: return ""
  return usuario + "_" + nota + "_" + dataHora + "_" + loc
}

interface ITabNotaExp : ITabView {
  fun filtro(marca: EMarcaNota): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNF>
}