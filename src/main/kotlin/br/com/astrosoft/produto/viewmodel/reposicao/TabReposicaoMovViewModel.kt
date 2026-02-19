package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintReposicaoMovimentacao
import br.com.astrosoft.produto.model.saci

class TabReposicaoMovViewModel(val viewModel: ReposicaoViewModel) {
  val subView
    get() = viewModel.view.tabReposicaoMov

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val user = AppConfig.userLogin() as? UserSaci

    val filtro = subView.filtro()
    val produtos = ProdutoMovimentacao.findAll(filtro).agrupa().sortedBy { it.numero }.filter { mov: Movimentacao ->
      if (user == null) {
        return@filter true
      }

      if (user.admin) {
        return@filter true
      }

      (mov.noGravado == user.no) || (mov.entregue == user.login)
    }
    subView.updatePedidos(produtos)
  }

  fun gravaPedido(pedido: Movimentacao) = viewModel.exec {
    if (pedido.noGravado > 0) {
      fail("Pedido já gravado")
    }

    val produtosSelecionados = subView.produtosSelecionado()
    if (produtosSelecionados.isEmpty()) {
      fail("Selecionar produtos para gravar pedido")
    }

    if(produtosSelecionados.any { (it.movimentacao ?: 0) == 0 }){
      fail("Não é possível gravar pedido com quantidade zero")
    }

    subView.autorizaPedido("Autoriza gravação do pedido") { user ->
      subView.gravaSelecao()
      produtosSelecionados.forEach {
        val userLogin = AppConfig.userLogin() as? UserSaci
        it.noLogin = userLogin?.no
        it.noGravado = user.no
        it.save()
      }
      updateView()
      subView.closeForm()
    }
  }

  fun removePedido(pedido: Movimentacao) = viewModel.exec {
    val itensSelecionado = subView.produtosSelecionado()

    itensSelecionado.ifEmpty {
      fail("Nenhum produto selecionado")
    }

    if (pedido.noEntregue > 0) {
      fail("Entrega assinada, produto não pode ser removido")
    }

    subView.autorizaPedido("Autoriza remover do produto") {
      itensSelecionado.forEach { produto ->
        produto.remove()
      }
      updateView()
    }
  }

  fun addProduto(produto: ProdutoMovimentacao) {
    produto.save()
    updateView()
  }

  fun findProdutos(codigo: String, loja: Int): List<PrdGrade> {
    return saci.findGrades(codigo, loja)
  }

  fun findFornecedor(vendno: Int?): Fornecedor? {
    vendno ?: return null
    return Fornecedor.findByVendno(vendno)
  }

  fun updateProduto(produtos: List<ProdutoMovimentacao>) {
    ProdutoMovimentacao.updateProduto(produtos)
  }

  fun novoPedido(numLoja: Int) = viewModel.exec {
    if (numLoja == 0) {
      fail("Selecione uma loja")
    }
    val novoPedido = createPedido(numLoja) ?: fail("Não foi possível criar o pedido")
    val pedido = listOf(novoPedido).agrupa().firstOrNull() ?: fail("Não foi possível criar o pedido de pedido")
    subView.adicionaPedido(pedido)
  }

  private fun createPedido(numLoja: Int): ProdutoMovimentacao? {
    val user = AppConfig.userLogin()
    val numero = ProdutoMovimentacao.proximoNumero(numLoja)
    val novo = saci.moviumentacaoNova(numero, numLoja) ?: return null

    return ProdutoMovimentacao(
      numero = novo.numero,
      numloja = novo.numloja,
      lojaSigla = novo.lojaSigla,
      data = novo.data,
      hora = novo.hora,
      login = user?.login,
      usuario = user?.name,
      prdno = null,
      descricao = null,
      grade = null,
    )
  }

  fun previewPedido(pedido: Movimentacao, printEvent: (impressora: String) -> Unit = {}) = viewModel.exec {
    val produtos: List<ProdutoMovimentacao> = pedido.findProdutos()

    val relatorio = PrintReposicaoMovimentacao()

    relatorio.print(
      dados = produtos.sortedWith(
        compareBy(
          ProdutoMovimentacao::descricao,
          ProdutoMovimentacao::codigo,
          ProdutoMovimentacao::grade
        )
      ),
      printer = subView.printerPreview(loja = 1, printEvent = printEvent)
    )
  }

  fun assinaEntrega(mov: Movimentacao) = viewModel.exec {
    if (mov.noGravado == 0) {
      fail("O produto não está gravado")
    }

    val pedidosSelecionado = subView.produtosSelecionado()
    val pedidosNaoSelecionado = subView.produtosNaoSelecionado()

    if (pedidosSelecionado.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    if (pedidosNaoSelecionado.isNotEmpty()) {
      fail("Produtos não selecionados")
    }

    if(mov.noEntregue > 0){
      fail("O produto já foi entregue")
    }

    subView.autorizaAssinatura("Entrega") { empno: Int, senha: String ->
      val funcionario = saci.listFuncionario(empno)

      if (funcionario?.senha != senha) {
        viewModel.view.showError("Funcionário ou senha inválido")
      }

      pedidosSelecionado.forEach {
        it.noEntregue = empno
        it.save()
      }
      subView.updateProdutos()
    }
  }

  fun assinaRecebimento(mov: Movimentacao) = viewModel.exec {
    if (mov.noGravado == 0) {
      fail("O produto não está gravado")
    }

    if (mov.noEntregue == 0) {
      fail("O produto ainda não foi entregue")
    }

    if(mov.noRecebido > 0){
      fail("O produto já foi recebido")
    }

    val pedidosSelecionado = subView.produtosSelecionado()
    val pedidosNaoSelecionado = subView.produtosNaoSelecionado()

    if (pedidosSelecionado.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    if (pedidosNaoSelecionado.isNotEmpty()) {
      fail("Produtos não selecionados")
    }

    subView.autorizaAssinatura("Recebimento") { empno: Int, senha: String ->
      val funcionario = saci.listFuncionario(empno)

      if (funcionario?.senha != senha) {
        viewModel.view.showError("Funcionário ou senha inválido")
      }

      pedidosSelecionado.forEach {
        it.noRecebido = empno
        it.save()
      }
      subView.updateProdutos()
    }
  }

  fun desfazAssinatura(movimentacao: Movimentacao) {
    val pedidosSelecionado = subView.produtosSelecionado()
    val pedidosNaoSelecionado = subView.produtosNaoSelecionado()

    if (pedidosSelecionado.isEmpty()) {
      fail("Nenhum pedido selecionado")
    }

    if (pedidosNaoSelecionado.isNotEmpty()) {
      fail("Pedidos não selecionados")
    }

    viewModel.view.showQuestion("Desfaz assinatura?") {
      movimentacao.findProdutos().forEach {
        it.noRecebido = 0
        it.noEntregue = 0
        it.save()
      }
      subView.updateProdutos()
    }
  }

  fun gravaRota(movimentacao: Movimentacao) {
    movimentacao.salvaRota()
  }
}

interface ITabReposicaoMov : ITabView {
  fun filtro(): FiltroMovimentacao
  fun updatePedidos(produtos: List<Movimentacao>)
  fun updateProdutos()
  fun itensSelecionados(): List<Movimentacao>
  fun filtroVazio(): FiltroProdutoEstoque
  fun autorizaPedido(caption: String, block: (user: IUser) -> Unit)
  fun autorizaAssinatura(assunto: String, block: (empno: Int, senha: String) -> Unit)
  fun produtosSelecionado(): List<ProdutoMovimentacao>
  fun produtosNaoSelecionado(): List<ProdutoMovimentacao>
  fun adicionaPedido(movimentacao: Movimentacao)
  fun gravaSelecao()
  fun closeForm()
}
