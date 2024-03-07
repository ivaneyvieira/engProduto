package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintRessuprimento
import br.com.astrosoft.produto.model.saci

class TabRessuprimentoEntViewModel(val viewModel: RessuprimentoViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaRessuprimento.ENT)
    val ressuprimento = Ressuprimento.find(filtro)
    subView.updateRessuprimentos(ressuprimento)
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

    if (pedido.transportadoPor.isNullOrBlank())
      fail("Pedido não transportado")

    val user = AppConfig.userLogin() as? UserSaci

    if (pedido.recebidoPor.isNullOrBlank() && user?.ressuprimentoRecebedor == true && !user.admin)
      fail("Pedido não recebido")

    val produtos = pedido.produtos(EMarcaRessuprimento.ENT)
    val valorTotal = produtos.sumOf { it.vlPedido ?: 0.00 }
    val relatorio = PrintRessuprimento(pedido, valorTotal, ProdutoRessuprimento::qtPedido)

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

  fun marcaImpressao(pedido: Ressuprimento) = viewModel.exec {
    val usuario = AppConfig.userLogin() as? UserSaci
    if (usuario?.ressuprimentoRecebedor == true) {
      if (pedido.recebidoPor.isNullOrBlank()) fail("Pedido não recebido")
      pedido.produtos(EMarcaRessuprimento.ENT).forEach {
        it.marca = EMarcaRessuprimento.REC.num
        it.salva()
      }
      updateView()
    }
  }

  fun selecionaProdutos(codigoBarra: String) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.selecionado = EMarcaRessuprimento.REC.num
    produto.salva()

    subView.updateProduto(produto)
  }

  fun marca() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter {
      val qtRecebido = it.qtRecebido ?: 0
      val qtPedido = it.qtPedido ?: 0
      it.selecionado == EMarcaRessuprimento.REC.num &&
      (qtRecebido == qtPedido) &&
      (it.codigoCorrecao.isNullOrBlank()) &&
      (it.gradeCorrecao.isNullOrBlank())
    }
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }

    itens.forEach { produto ->
      produto.marca = EMarcaRessuprimento.REC.num
      produto.selecionado = EMarcaRessuprimento.REC.num
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun desmarcar() = viewModel.exec {
    val itens = subView.produtosSelecionados().filter { it.selecionado == EMarcaRessuprimento.REC.num }
    itens.ifEmpty {
      fail("Nenhum produto para desmarcar")
    }

    itens.forEach { produto ->
      produto.selecionado = 0
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun saveQuant(bean: ProdutoRessuprimento) {
    bean.salva()
    subView.updateProdutos()
  }

  fun findGrades(codigo: String): List<String> {
    return saci.findGrades(codigo).map { it.grade }
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
  fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento?
  fun updateProduto(produto: ProdutoRessuprimento)
  fun produtosSelecionados(): List<ProdutoRessuprimento>
}