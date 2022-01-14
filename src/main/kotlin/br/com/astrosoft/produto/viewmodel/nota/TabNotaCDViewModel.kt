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

class TabNotaCDViewModel(val viewModel: NotaViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaNota.CD)
    val notas = NotaSaida.find(filtro)
    subView.updateNotas(notas)
  }

  fun marcaExp() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaNota.EXP.num
      produtoNF.usuarioExp = ""
      produtoNF.usuarioCD = ""
      produtoNF.salva()
    }
    subView.updateProdutos()
    updateView()
  }

  fun marcaEntProdutos(codigoBarra: String) = viewModel.exec {
    val produtoNF = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produtoNF.marca = EMarcaNota.ENT.num
    val dataHora = LocalDate.now().format() + "_" + LocalTime.now().format()
    val usuario = Config.user?.login ?: ""
    produtoNF.usuarioCD = usuario + "_" + dataHora
    produtoNF.salva()
    subView.updateProdutos()
    updateView()
  }

  fun printEtiqueta(nota: NotaSaida?) = viewModel.exec {
    val chave = nota?.chaveExp ?: fail("Chave não encontrada")
    val split = chave.split("_")
    val user = Config.user as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreview(impressora,
                                   DadosEtiqueta(titulo = "Exp",
                                                 usuario = split.getOrNull(0) ?: "",
                                                 nota = split.getOrNull(1) ?: "",
                                                 data = split.getOrNull(2) ?: "",
                                                 hora = split.getOrNull(3) ?: "",
                                                 local = split.getOrNull(4)
                                                         ?: "")) //viewModel.showInformation("Impressão realizada na impressora $impressora")
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  val subView
    get() = viewModel.view.tabNotaCD
}

interface ITabNotaCD : ITabView {
  fun filtro(marca: EMarcaNota): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNF>
  fun produtosCodigoBarras(codigoBarra: String): ProdutoNF?
}