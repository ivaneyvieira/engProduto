package br.com.astrosoft.produto.viewmodel.expedicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabNotaCDViewModel(val viewModel: NotaViewModel) {
  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val notas = NotaSaida.find(filtro).filter {
      it.cancelada != "S"
    }
    subView.updateNotas(notas)
  }

  fun marcaExp() = viewModel.exec {
    val itens = subView.produtosSelecionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaNota.EXP.num
      produtoNF.usernoExp = 0
      produtoNF.usernoCD = 0
      produtoNF.salva()
    }
    subView.updateProdutos()
    updateView()
  }

  fun marcaEnt() = viewModel.exec {
    val itens = subView.produtosSelecionados()

    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }

    subView.formAutoriza(itens) { userno ->
      itens.forEach { produtoNF ->
        produtoNF.marca = EMarcaNota.ENT.num
        produtoNF.usernoCD = userno
        produtoNF.salva()
      }
      subView.updateProdutos()
      updateView()
    }
  }

  fun marcaEntProdutos() = viewModel.exec {
    val produtosNaoMarcados = subView.produtosNaoMarcados()
    var continua = false
    if (produtosNaoMarcados.isNotEmpty()) {
      viewModel.view.showQuestion("Existem produtos não marcados. Deseja continuar?") {
        continua = true
      }
    } else {
      continua = true
    }
    if (continua) {
      val produtos = subView.produtosMarcados()
      subView.formAutoriza(produtos) { userno ->
        produtos.forEach { produtoNF ->
          produtoNF.marca = EMarcaNota.ENT.num
          produtoNF.usernoCD = userno
          produtoNF.salva()
        }
        subView.updateProdutos()
        val nota = subView.findNota() ?: fail("Nota não encontrada")
        val produtosRestantes = nota.produtos(EMarcaNota.CD, todosLocais = false)
        if (produtosRestantes.isEmpty()) {
          imprimeEtiquetaEnt(nota.produtos(EMarcaNota.ENT, todosLocais = false))
        }
      }
    }
  }

  fun selecionaProduto(codigoBarra: String) = viewModel.exec {
    val produtoList = subView.produtosCodigoBarras(codigoBarra).ifEmpty {
      fail("Produto não encontrado")
    }

    produtoList.forEach { produto ->
      produto.selecionado = true
    }
  }

  private fun imprimeEtiquetaEnt(produtos: List<ProdutoNFS>) {
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressoraNota?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, produtos, 2)
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
        EtiquetaChave.printPreviewExp(impressora, nota.produtos(EMarcaNota.CD, todosLocais = false), 2)
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
        produto.usernoCD = user.no
        produto.salva()
      }
    }

    return user
  }

  val subView
    get() = viewModel.view.tabNotaCD
}

interface ITabNotaCD : ITabView {
  fun filtro(): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun updateProdutos()
  fun produtosSelecionados(): List<ProdutoNFS>
  fun produtosMarcados(): List<ProdutoNFS>
  fun produtosNaoMarcados(): List<ProdutoNFS>
  fun produtosCodigoBarras(codigoBarra: String): List<ProdutoNFS>
  fun findNota(): NotaSaida?
  fun formAutoriza(lista: List<ProdutoNFS>, marca: (userno: Int) -> Unit)
}