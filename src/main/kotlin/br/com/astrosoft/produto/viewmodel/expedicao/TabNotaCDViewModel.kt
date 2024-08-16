package br.com.astrosoft.produto.viewmodel.expedicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.EtiquetaChave
import java.time.LocalDate
import java.time.LocalTime

class TabNotaCDViewModel(val viewModel: NotaViewModel) {
  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
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

  fun marcaEnt() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaNota.ENT.num
      val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
      val usuario = AppConfig.userLogin()?.login ?: ""
      produtoNF.usuarioCD = "$usuario-$dataHora"
      produtoNF.salva()
    }
    subView.updateProdutos()
    updateView()
  }

  fun marcaEntProdutos(codigoBarra: String) = viewModel.exec {
    val produtoNF = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produtoNF.marca = EMarcaNota.ENT.num
    val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
    val usuario = AppConfig.userLogin()?.login ?: ""
    produtoNF.usuarioCD = "$usuario-$dataHora"
    produtoNF.salva()
    subView.updateProdutos()
    val nota = subView.findNota() ?: fail("Nota não encontrada")
    val produtosRestantes = nota.produtos(EMarcaNota.CD)
    if (produtosRestantes.isEmpty()) {
      imprimeEtiquetaEnt(nota.produtos(EMarcaNota.ENT))
    }
  }

  private fun imprimeEtiquetaEnt(produtos: List<ProdutoNFS>) {
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressoraNota?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, produtos)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  fun printEtiquetaExp(nota: NotaSaida?) = viewModel.exec {
    nota ?: fail("Nenhuma expedicao selecionada")
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressoraNota?.let { impressora ->
      try {
        EtiquetaChave.printPreviewExp(impressora, nota.produtos(EMarcaNota.CD))
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  fun findGrade(prd: ProdutoNFS?, block: (List<PrdGrade>) -> Unit) = viewModel.exec {
    prd ?: return@exec
    val list = prd.findGrades()
    block(list)
  }

  fun formEntregue(nota: NotaSaida) {
    subView.formEntregue(nota)
  }

  fun entreguePedido(nota: NotaSaida, login: String, senha: String) {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    nota.entregue(user)

    updateView()
  }

  val subView
    get() = viewModel.view.tabNotaCD
}

interface ITabNotaCD : ITabView {
  fun filtro(): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNFS>
  fun produtosCodigoBarras(codigoBarra: String): ProdutoNFS?
  fun findNota(): NotaSaida?
  fun formEntregue(saida: NotaSaida)
}