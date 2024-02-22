package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintRessuprimento
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabRessuprimentoEntViewModel(val viewModel: RessuprimentoViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaRessuprimento.ENT)
    val ressuprimento = Ressuprimento.find(filtro)
    subView.updateRessuprimentos(ressuprimento)
  }

  fun marcaCD() {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaRessuprimento.CD.num
      produtoNF.usuarioCD = ""
      produtoNF.salva()
    }
    subView.updateProdutos()
  }

  fun printEtiqueta(ressuprimento: Ressuprimento?) = viewModel.exec {
    ressuprimento ?: fail("Nenhum ressuprimento selecionado")
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, ressuprimento.produtos(EMarcaRessuprimento.ENT))
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  fun formAutoriza(pedido: Ressuprimento) {
    subView.formAutoriza(pedido)
  }

  fun autorizaPedido(pedido: Ressuprimento, login: String, senha: String) {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    pedido.autoriza(user)

    updateView()
  }

  fun previewPedido(pedido: Ressuprimento, printEvent: (impressora: String) -> Unit) = viewModel.exec {
    if (pedido.singno == 0)
      fail("Pedido não autorizado")
    val produtos = pedido.produtos(EMarcaRessuprimento.ENT)
    val relatorio = PrintRessuprimento()

    relatorio.print(
      dados = produtos,
      printer = subView.printerPreview(loja = 1, printEvent = printEvent)
    )
  }

  val subView
    get() = viewModel.view.tabRessuprimentoEnt
}

interface ITabRessuprimentoEnt : ITabView {
  fun filtro(marca: EMarcaRessuprimento): FiltroRessuprimento
  fun updateRessuprimentos(ressuprimentos: List<Ressuprimento>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoRessuprimento>
  fun formAutoriza(pedido: Ressuprimento)
}