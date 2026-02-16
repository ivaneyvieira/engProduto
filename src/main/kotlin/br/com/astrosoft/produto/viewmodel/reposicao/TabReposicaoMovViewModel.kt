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
    val produtos = ProdutoMovimentacao.findAll(filtro).agrupa().sortedBy { it.numero }.filter {
      if (user == null) {
        return@filter true
      }

      if (user.admin) {
        return@filter true
      }

      (it.usuario == user.name) || (it.login == user.login)
    }
    subView.updateProduto(produtos)
  }

  fun updateProduto(produto: ProdutoMovimentacao) = viewModel.exec {
    produto.save()
  }

  fun gravaPedido(pedido: Movimentacao) = viewModel.exec {
    if (pedido.noGravado > 0) {
      fail("Pedido já gravado")
    }
    subView.autorizaPedido { user ->
      subView.gravaSelecao()
      val pordutos = pedido.findProdutos()
      pordutos.forEach {
        it.noGravado = user.no
        it.save()
      }
      updateView()
      subView.closeForm()
    }
  }

  fun removePedido() = viewModel.exec {
    val itensSelecionado = subView.produtosSelecionado()

    itensSelecionado.ifEmpty {
      fail("Nenhum Pedido selecionado")
    }

    subView.autorizaPedido {
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
}

interface ITabReposicaoMov : ITabView {
  fun filtro(): FiltroMovimentacao
  fun updateProduto(produtos: List<Movimentacao>)
  fun itensSelecionados(): List<Movimentacao>
  fun filtroVazio(): FiltroProdutoEstoque
  fun autorizaPedido(block: (user: IUser) -> Unit)
  fun produtosSelecionado(): List<ProdutoMovimentacao>
  fun adicionaPedido(movimentacao: Movimentacao)
  fun gravaSelecao()
  fun closeForm()
}
