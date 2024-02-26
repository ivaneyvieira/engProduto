package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintRessuprimento
import br.com.astrosoft.produto.model.saci
import br.com.astrosoft.produto.model.zpl.EtiquetaChave

class TabRessuprimentoEntViewModel(val viewModel: RessuprimentoViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaRessuprimento.ENT)
    val ressuprimento = Ressuprimento.find(filtro)
    subView.updateRessuprimentos(ressuprimento)
  }

  fun marcaCD() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produtoNF ->
      produtoNF.marca = EMarcaRessuprimento.CD.num
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

  fun recebidoPedido(pedido: Ressuprimento, login: String, senha: String) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    pedido.autorizaRecebido(user)

    updateView()
  }

  fun autorizaPedido(pedido: Ressuprimento, login: String, senha: String) = viewModel.exec {
    val lista = UserSaci.findAll()
    val user = lista
      .firstOrNull {
        it.login.uppercase() == login.uppercase() && it.senha.uppercase().trim() == senha.uppercase().trim()
      }
    user ?: fail("Usuário ou senha inválidos")

    pedido.autoriza(user)

    updateView()
  }

  fun entreguePedido(pedido: Ressuprimento, numero: Int) = viewModel.exec {
    val funcionario = saci.listFuncionario(numero) ?: fail("Funcionário não encontrado")
    pedido.entregue(funcionario)
    updateView()
  }

  fun recebePedido(pedido: Ressuprimento, numero: Int) = viewModel.exec {
    val funcionario = saci.listFuncionario(numero) ?: fail("Funcionário não encontrado")
    pedido.recebe(funcionario)
    updateView()
  }

  fun transportadoPedido(pedido: Ressuprimento, numero: Int) = viewModel.exec {
    val funcionario = saci.listFuncionario(numero) ?: fail("Funcionário não encontrado")
    pedido.transportado(funcionario)
    updateView()
  }

  fun previewPedido(pedido: Ressuprimento, printEvent: (impressora: String) -> Unit) = viewModel.exec {
    if (pedido.sing.isNullOrBlank())
      fail("Pedido não autorizado")

    if(pedido.recebidoPor.isNullOrBlank())
      fail("Pedido não recebido"      )

    if(pedido.transportadoPor.isNullOrBlank())
      fail("Pedido não transportado")

    val produtos = pedido.produtos(EMarcaRessuprimento.ENT)
    val relatorio = PrintRessuprimento(pedido)

    relatorio.print(
      dados = produtos.sortedWith(
        compareBy(
          ProdutoRessuprimento::descricao,
          ProdutoRessuprimento::codigo,
          ProdutoRessuprimento::grade
        )
      ),
      printer = subView.printerPreview(loja = 1, printEvent = printEvent)
    )
  }

  fun formTransportado(pedido: Ressuprimento) {
    subView.formTransportado(pedido)
  }

  fun formRecebido(pedido: Ressuprimento) {
    subView.formRecebido(pedido)
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
  fun formTransportado(pedido: Ressuprimento)
  fun formRecebido(pedido: Ressuprimento)
}