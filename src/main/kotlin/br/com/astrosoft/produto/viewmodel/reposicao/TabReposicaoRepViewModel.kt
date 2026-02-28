package br.com.astrosoft.produto.viewmodel.reposicao

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.PrintReposicaoMovimentacao
import br.com.astrosoft.produto.model.saci
import br.com.astrosoft.produto.viewmodel.estoqueCD.ProcessamentoKardec
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalTime

class TabReposicaoRepViewModel(val viewModel: ReposicaoViewModel) {
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

    if (pedido.noRota == null) {
      fail("Pedido sem rota definida")
    }

    val produtos = subView.produtos()


    if (produtos.any { (it.movimentacao ?: 0) == 0 }) {
      fail("Não é possível gravar pedido com quantidade zero")
    }

    subView.autorizaPedido("Autoriza gravação do pedido") { user ->
      subView.gravaSelecao()
      produtos.forEach {
        val userLogin = AppConfig.userLogin() as? UserSaci
        it.noLogin = userLogin?.no
        it.noGravado = user.no
        it.noRota = pedido.noRota
        it.save()
      }
      atualizaKardec(pedido)
      updateView()
      viewModel.view.showInformation("Pedido gravado com sucesso")
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

    if (mov.noRota == null) {
      fail("Pedido sem rota definida")
    }

    val produtos = subView.produtos()

    if (mov.noEntregue > 0) {
      fail("O produto já foi entregue")
    }

    subView.autorizaAssinatura("Entrega") { login: String, senha: String ->
      val user = UserSaci.findUser(login).firstOrNull() ?: fail("Não encontrado usuário do funcionário")

      if (user.senha != senha) {
        viewModel.view.showError("Funcionário ou senha inválido")
        return@autorizaAssinatura
      }

      if (mov.enumRota == ERota.CD_LJ && !user.reposicaoUsuarioCD) {
        viewModel.view.showError("Usuário não autorizado para assinar entrega da rota CD/LJ")
        return@autorizaAssinatura
      }

      if (mov.enumRota == ERota.LJ_CD && !user.reposicaoUsuarioLJ) {
        viewModel.view.showError("Usuário não autorizado para assinar entrega da rota LJ/CD")
        return@autorizaAssinatura
      }

      produtos.forEach {
        it.noEntregue = user.no
        it.dataEntrege = LocalDate.now()
        it.horaEntrege = LocalTime.now()
        it.noRota = mov.noRota
        it.save()
      }
      atualizaKardec(mov)
      subView.updateProdutos()
      viewModel.view.showInformation("Entrega Assinada com sucesso")
    }
  }

  fun assinaRecebimento(mov: Movimentacao) = viewModel.exec {
    if (mov.noGravado == 0) {
      fail("O produto não está gravado")
    }

    if (mov.noRota == null) {
      fail("Pedido sem rota definida")
    }

    if (mov.noEntregue == 0) {
      fail("O produto ainda não foi entregue")
    }

    if (mov.noRecebido > 0) {
      fail("O produto já foi recebido")
    }

    val produtos = subView.produtos()

    subView.autorizaAssinatura("Recebimento") { login: String, senha: String ->
      val user = UserSaci.findUser(login).firstOrNull() ?: fail("Não encontrado usuário do funcionário")

      if (user.senha != senha) {
        viewModel.view.showError("Funcionário ou senha inválido")
        return@autorizaAssinatura
      }

      if (mov.enumRota == ERota.CD_LJ && !user.reposicaoUsuarioLJ) {
        viewModel.view.showError("Usuário não autorizado para assinar recebimento da rota CD/LJ")
        return@autorizaAssinatura
      }

      if (mov.enumRota == ERota.LJ_CD && !user.reposicaoUsuarioCD) {
        viewModel.view.showError("Usuário não autorizado para assinar recebimento da rota LJ/CD")
        return@autorizaAssinatura
      }

      produtos.forEach {
        it.noRecebido = user.no
        it.dataRecebido = LocalDate.now()
        it.horaRecebido = LocalTime.now()
        it.noRota = mov.noRota
        it.save()
      }
      atualizaKardec(mov)
      subView.updateProdutos()
      viewModel.view.showInformation("Recebimento Assinado com sucesso")
    }
  }

  fun desfazAssinatura(movimentacao: Movimentacao) = viewModel.exec {
    val pedidosSelecionado = subView.produtosSelecionado()
    val pedidosNaoSelecionado = subView.produtosNaoSelecionado()

    if (pedidosSelecionado.isEmpty()) {
      fail("Nenhum pedido selecionado")
    }

    if (pedidosNaoSelecionado.isNotEmpty()) {
      fail("Pedidos não selecionados")
    }

    viewModel.view.showQuestion("Desfaz assinatura?") {
      val produtos = movimentacao.findProdutos()
      produtos.forEach {
        it.noRecebido = 0
        it.noEntregue = 0
        it.noRota = null
        it.save()
      }
      atualizaKardec(movimentacao)
      subView.updateProdutos()
    }
  }

  private fun atualizaKardec(pedido: Movimentacao) = runBlocking {
    if (pedido.noEntregue > 0) {
      return@runBlocking
    }
    if (pedido.noEntregue == 0 || pedido.noGravado == 0) {
      return@runBlocking
    }

    val produtos = pedido.findProdutos()
    val produtosKad = produtos.flatMap { produto ->
      val produtoEstoque = ProdutoEstoque.findProdutoEstoque(
        loja = produto.numloja ?: 0,
        prdno = produto.prdno ?: "",
        grade = produto.grade ?: "",
      )
      produtoEstoque
    }
    launch {
      ProcessamentoKardec.updateKardec(produtosKad)
    }
    launch {
      ProcessamentoKardec.updateControleKardec(produtosKad)
    }
  }

  fun gravaRota(movimentacao: Movimentacao) {
    movimentacao.salvaRota()
    atualizaKardec(movimentacao)
    subView.updateProdutos()
  }
}

interface ITabReposicaoRep : ITabView {
  fun filtro(): FiltroMovimentacao
  fun updatePedidos(produtos: List<Movimentacao>)
  fun updateProdutos()
  fun itensSelecionados(): List<Movimentacao>
  fun filtroVazio(): FiltroProdutoEstoque
  fun autorizaPedido(caption: String, block: (user: IUser) -> Unit)
  fun autorizaAssinatura(assunto: String, block: (login: String, senha: String) -> Unit)
  fun produtos(): List<ProdutoMovimentacao>
  fun produtosSelecionado(): List<ProdutoMovimentacao>
  fun produtosNaoSelecionado(): List<ProdutoMovimentacao>
  fun adicionaPedido(movimentacao: Movimentacao)
  fun gravaSelecao()
  fun closeForm()
}
