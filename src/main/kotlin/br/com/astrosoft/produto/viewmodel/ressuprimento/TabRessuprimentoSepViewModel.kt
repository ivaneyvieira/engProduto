package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintRessuprimento
import br.com.astrosoft.produto.model.saci

class TabRessuprimentoSepViewModel(val viewModel: RessuprimentoViewModel) {
    fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

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
    if (pedido.entreguePor.isNullOrBlank())
      fail("Pedido não autorizado")

    if (pedido.transportadoPor.isNullOrBlank())
      fail("Pedido não transportado")

    val user = AppConfig.userLogin() as? UserSaci

    if (pedido.recebidoPor.isNullOrBlank() && user?.ressuprimentoRecebedor == true && !user.admin)
      fail("Pedido não recebido")

    val produtos = pedido.produtos()

    val relatorio = PrintRessuprimento(pedido, ProdutoRessuprimento::qtPedido)

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
      pedido.produtos().forEach {
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

  fun saveQuant(bean: ProdutoRessuprimento) {
    bean.salva()
    subView.updateProdutos()
  }

  fun findGrades(codigo: String): List<PrdGrade> {
    return saci.findGrades(codigo)
  }

  fun processamentoProdutos() {
    val selecionados = subView.ressuprimentosSelecionados()
    if(selecionados.isEmpty()) fail("Nenhum ressuprimento selecionado")
    subView.showDlgProdutos(selecionados)
  }

  val subView
    get() = viewModel.view.tabRessuprimentoSep
}

interface ITabRessuprimentoSep : ITabView {
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
  fun ressuprimentosSelecionados(): List<Ressuprimento>
  fun showDlgProdutos(ressuprimentos: List<Ressuprimento>)
}