package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.ProdutosDevolucao
import java.time.LocalDate
import java.time.LocalTime

class TabEstoqueDevProdutoViewModel(val viewModel: EstoqueCDViewModel) {
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
    val produtosFiltrados = produtos.filter {
      it.tipoNotaPre.endsWith(" P")
    }

    subView.updateProdutos(produtosFiltrados)
  }

  fun imprimeProdutos() = viewModel.exec {
    val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Não há produtos selecionados")
    }

    if (produtos.any { it.userEntregaNo == 0 || it.userEntregaNo == null }) {
      fail("Possui produtos com entrega não autorizada")
    }

    if (produtos.any { it.userRecebimentoNo == 0 || it.userRecebimentoNo == null }) {
      fail("Possui produtos com recebimento não autorizada")
    }

    val countEntregador = produtos.map { it.userEntregaNo ?: 0 }.distinct().size
    if (countEntregador != 1) {
      fail("Possui mais de um entregador")
    }

    val countRecebedor = produtos.map { it.userRecebimentoNo ?: 0 }.distinct().size
    if (countRecebedor != 1) {
      fail("Possui mais de um recebedor")
    }

    val countTipo = produtos.map { it.produtoTipoP }.distinct().size
    if (countTipo != 1) {
      fail("Foi seleciona produtos de mais de um tipo")
    }

    val relatorio = ProdutosDevolucao("Devolucoes de Clientes com Produtos")
    relatorio.print(produtos.sortedBy { it.ni }, subView.printerPreview(loja = 0))
  }

  fun updateKardex() = viewModel.exec {
    val produtos = subView.produtosSelecionados()
      .flatMap {
        ProdutoEstoque.findProdutoEstoque(loja = it.codLoja, prdno = it.prdno, grade = it.grade)
      }
    ProcessamentoKardec.updateKardex(produtos, ETipoKardec.DEVOLUCAO)
    subView.reloadGrid()
  }

  fun validaLogin(login: String, senha: String): UserSaci? {
    return UserSaci.userLogin(login, senha)
  }

  fun autorizaEntrega() = viewModel.exec {
    val produtos: List<EntradaDevCliProList> = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }

    val countLog = produtos.map { it.localizacao ?: "" }.distinct().size
    if (countLog != 1) {
      fail("Foi seleciona produtos de mais de uma localização")
    }

    val countTipo = produtos.map { it.produtoTipoP }.distinct().size
    if (countTipo != 1) {
      fail("Foi seleciona produtos de mais de um tipo")
    }

    if (produtos.any { it.userEntregaNo != 0 && it.userEntregaNo != null }) {
      fail("Tem produto que já foi entregue")
    }

    subView.autorizaEntrega(produtos) { user, produtos ->
      produtos.forEach { produto ->
        produto.userEntregaNo = user.no
        produto.dataEntrega = LocalDate.now()
        produto.horaEntrega = LocalTime.now()

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

    val countLog = produtos.map { it.localizacao ?: "" }.distinct().size
    if (countLog != 1) {
      fail("Foi seleciona produtos de mais de uma localização")
    }

    val countTipo = produtos.map { it.produtoTipoP }.distinct().size
    if (countTipo != 1) {
      fail("Foi seleciona produtos de mais de um tipo")
    }

    if (produtos.any {
        val userEntrega = it.userEntregaNo ?: 0
        userEntrega == 0
      }) {
      fail("Não pode receber produto não entregue")
    }

    if (produtos.any { it.userRecebimentoNo != 0 && it.userRecebimentoNo != null }) {
      fail("Tem produto que já foi recebido")
    }

    subView.autorizaRecebimento(produtos) { user, produtos ->
      produtos.forEach { produto ->
        produto.userRecebimentoNo = user.no
        produto.dataRecebimento = LocalDate.now()
        produto.horaRecebimento = LocalTime.now()
        produto.salvaAutorizacao()
      }
      updateView()
    }
  }

  fun desfazerAutorizacao() = viewModel.exec {
    val produtos: List<EntradaDevCliProList> = subView.produtosSelecionados()
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    produtos.forEach { produto ->
      produto.userEntregaNo = 0
      produto.userRecebimentoNo = 0
      produto.dataEntrega = null
      produto.dataRecebimento = null
      produto.horaEntrega = null
      produto.horaRecebimento = null
      produto.salvaAutorizacao()
    }
    updateView()
  }

  val subView
    get() = viewModel.view.tabEstoqueDevProduto
}

interface ITabEstoqueDevProduto : ITabView {
  fun filtro(): FiltroEntradaDevCliProList
  fun updateProdutos(produtos: List<EntradaDevCliProList>)
  fun produtosSelecionados(): List<EntradaDevCliProList>
  fun reloadGrid()

  fun autorizaEntrega(
    produtos: List<EntradaDevCliProList>,
    block: (user: UserSaci, produtos: List<EntradaDevCliProList>) -> Unit
  )

  fun autorizaRecebimento(
    produtos: List<EntradaDevCliProList>,
    block: (user: UserSaci, produtos: List<EntradaDevCliProList>) -> Unit
  )
}