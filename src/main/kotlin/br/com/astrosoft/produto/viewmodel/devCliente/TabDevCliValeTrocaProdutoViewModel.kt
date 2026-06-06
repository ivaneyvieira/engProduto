package br.com.astrosoft.produto.viewmodel.devCliente

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.EntradaDevCliProList
import br.com.astrosoft.produto.model.beans.FiltroEntradaDevCliProList
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.printText.ProdutosDevolucao

class TabDevCliValeTrocaProdutoViewModel(val viewModel: DevClienteViewModel) {
  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() {
    val filtro = subView.filtro()
    val produtos = EntradaDevCliProList.findAll(filtro)
    subView.updateProdutos(produtos)
  }

  fun imprimeProdutos() = viewModel.exec {
    val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Não há produtos selecionados")
    }
    val relatorio = ProdutosDevolucao("Devolucoes de Clientes com Produtos")
    relatorio.print(produtos.sortedBy { it.ni }, subView.printerPreview(loja = 0))
  }

  fun autorizaEntrega() = viewModel.exec {
    val produtos: List<EntradaDevCliProList> = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val countLog = produtos.map { it.localizacao ?: "" }.size
    if (countLog != 1) {
      fail("Foi seleciona produtos de mais de uma localização")
    }

    subView.autorizaEntrega(produtos) { user, produtos ->
      produtos.forEach { produto ->
        produto.userEntregaNo = user.no
        produto.salvaAutorizacao()
      }
      updateView()
    }
  }

  fun autorizaRecebimento() = viewModel.exec {
    val produtos: List<EntradaDevCliProList> = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val countLog = produtos.map { it.localizacao ?: "" }.size
    if (countLog != 1) {
      fail("Foi seleciona produtos de mais de uma localização")
    }

    if (produtos.any {
        val userEntrega = it.userEntregaNo ?: 0
        userEntrega == 0
      }) {
      fail("Não pode receber produto não entregue")
    }

    subView.autorizaRecebimento(produtos) { user, produtos ->
      produtos.forEach { produto ->
        produto.userRecebimentoNo = user.no
        produto.salvaAutorizacao()
      }
      updateView()
    }
  }

  fun validaLogin(login: String, senha: String): UserSaci? {
    return UserSaci.userLogin(login, senha)
  }

  val subView
    get() = viewModel.view.tabDevCliValeTrocaProduto
}

interface ITabDevCliValeTrocaProduto : ITabView {
  fun filtro(): FiltroEntradaDevCliProList
  fun updateProdutos(produtos: List<EntradaDevCliProList>)
  fun produtosSelecionados(): List<EntradaDevCliProList>

  fun autorizaEntrega(
    produtos: List<EntradaDevCliProList>,
    block: (user: UserSaci, produtos: List<EntradaDevCliProList>) -> Unit
  )

  fun autorizaRecebimento(
    produtos: List<EntradaDevCliProList>,
    block: (user: UserSaci, produtos: List<EntradaDevCliProList>) -> Unit
  )
}