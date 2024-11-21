package br.com.astrosoft.produto.viewmodel.expedicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.NotaExpedicao
import br.com.astrosoft.produto.model.printText.NotaExpedicaoEF
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabNotaTrocaViewModel(val viewModel: NotaViewModel) {
  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val user = AppConfig.userLogin() as? UserSaci
    val marca = if (user?.admin == true)
      EMarcaNota.TODOS
    else
      EMarcaNota.EXP
    val filtro = subView.filtro(marca)
    val notas = NotaSaida.find(filtro).filter {nota ->
      nota.tipoNotaSaida == ETipoNotaFiscal.ENTRE_FUT.name ||
      nota.tipoNotaSaida == ETipoNotaFiscal.SIMP_REME_L.name ||
      nota.tipoNotaSaida == ETipoNotaFiscal.SIMP_REME.name
    }
    subView.updateNotas(notas)
  }

  fun findGrade(prd: ProdutoNFS?, block: (List<PrdGrade>) -> Unit) = viewModel.exec {
    prd ?: return@exec
    val list = prd.findGrades()
    block(list)
  }

  fun marcaCD() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }

    itens.forEach {
      if(it.local.isNullOrBlank()) fail("Produto sem localização")
    }

    subView.formAutoriza(itens) { userno ->
      itens.forEach { produtoNF ->
        if (produtoNF.local.isNullOrBlank()) {
          fail("Produto sem localização")
        }
        produtoNF.marca = EMarcaNota.CD.num
        produtoNF.usernoExp = userno
        produtoNF.usernoCD = 0
        produtoNF.salva()
      }

      //TODO Testa filtro vazio
      imprimeEtiqueta(itens)
      subView.updateProdutos()
    }
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

  fun imprimeProdutosNota(nota: NotaSaida, itensSelecionados: List<ProdutoNFS>) = viewModel.exec {
    if (itensSelecionados.isEmpty())
      fail("Nenhum produto selecionado")
    if (nota.cancelada == "S")
      fail("Nota cancelada")
    val tipo = nota.tipoNotaSaida ?: ""
    val report = if (tipo == "ENTRE_FUT") NotaExpedicaoEF(nota) else NotaExpedicao(nota)
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

  val subView
    get() = viewModel.view.tabNotaTroca
}

interface ITabNotaTroca : ITabView {
  fun filtro(marca: EMarcaNota): FiltroNota
  fun updateNotas(notas: List<NotaSaida>)
  fun findNota(): NotaSaida?
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoNFS>
  fun formAutoriza(lista: List<ProdutoNFS>, marca: (userno: Int) -> Unit)
}