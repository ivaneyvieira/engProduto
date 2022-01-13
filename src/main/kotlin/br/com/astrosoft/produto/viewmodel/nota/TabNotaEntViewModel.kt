package br.com.astrosoft.produto.viewmodel.nota

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.DadosEtiqueta
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabNotaEntViewModel(val viewModel: NotaViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaNota.ENT)
    val notas = NotaSaida.find(filtro)
    subView.updateNotas(notas)
  }

  fun marcaCD() {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaNota.CD.num
      produtoNF.usuarioCD = ""
      produtoNF.salva()
    }
    subView.updateProdutos()
    updateView()
  }

  fun printEtiqueta(nota: NotaSaida?) = viewModel.exec {
    val chave = nota?.chaveCD ?: fail("Chave não encontrada")
    val split = chave.split("_")
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreview(impressora,
                            DadosEtiqueta(titulo = "Entrega",
                                          usuario = split.getOrNull(1) ?: "",
                                          nota = split.getOrNull(2) ?: "",
                                          data = split.getOrNull(3) ?: "",
                                          hora = split.getOrNull(4) ?: "",
                                          local = split.getOrNull(5) ?: ""))
        //viewModel.showInformation("Impressão realizada na impressora $impressora")
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  val subView
    get() = viewModel.view.tabNotaEnt
}

interface ITabNotaEnt : ITabView {
  fun filtro(marca: EMarcaNota): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNF>
}