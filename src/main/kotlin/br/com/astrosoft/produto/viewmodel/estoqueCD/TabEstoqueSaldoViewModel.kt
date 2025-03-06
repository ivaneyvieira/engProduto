package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.FiltroProdutoEstoque
import br.com.astrosoft.produto.model.beans.Loja
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoKardec
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoEstoque
import br.com.astrosoft.produto.model.printText.PrintProdutosEstoque
import java.time.LocalDate

class TabEstoqueSaldoViewModel(val viewModel: EstoqueCDViewModel) {
  val subView
    get() = viewModel.view.tabEstoqueSaldo

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun updateView() = viewModel.exec {
    val filtro = subView.filtro()
    val produtos = ProdutoEstoque.findProdutoEstoque(filtro)
    subView.updateProduto(produtos)
  }

  fun geraPlanilha(produtos: List<ProdutoEstoque>): ByteArray {
    val planilha = PlanilhaProdutoEstoque()
    return planilha.write(produtos)
  }

  private fun fetchKardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    val date = produto.dataInicialDefault
    val lista: List<ProdutoKardec> =
        produto.recebimentos(date) +
        produto.ressuprimento(date) +
        produto.expedicao(date) +
        produto.reposicao(date) +
        produto.saldoAnterior(date) +
        produto.acertoEstoque(date)
    var saldoAcumulado = 0
    return lista.ajustaOrdem()
  }

  private fun fetchKardecHoje(produto: ProdutoEstoque): List<ProdutoKardec> {
    val date = LocalDate.now()
    val lista: List<ProdutoKardec> =
        produto.recebimentos(date) +
        produto.ressuprimento(date) +
        produto.expedicao(date) +
        produto.reposicao(date) +
        produto.acertoEstoque(date)
    return lista.ajustaOrdem()
  }

  private fun List<ProdutoKardec>.ajustaOrdem(): List<ProdutoKardec> {
    var saldoAcumulado = 0
    return this.distinctBy { "${it.loja}${it.prdno}${it.grade}${it.data}${it.doc}${it.tipo}" }
      .sortedWith(compareBy({ it.data }, { it.loja }, { it.doc })).map {
        saldoAcumulado += (it.qtde ?: 0)
        it.copy(saldo = saldoAcumulado)
      }
  }

  fun updateKardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    ProdutoKardec.deleteList(produto)
    val list = fetchKardec(produto)
    list.filter { produtoKardec: ProdutoKardec ->
      true // produtoKardec.data?.isBefore(LocalDate.now()) ?: true
    }.forEach { produtoKardec: ProdutoKardec ->
      produtoKardec.save()
    }
    produto.dataUpdate = LocalDate.now()
    produto.update()
    return list.ajustaOrdem()
  }

  fun kardec(produto: ProdutoEstoque): List<ProdutoKardec> {
    if (produto.dataUpdate != LocalDate.now()) {
      val kardec = updateKardec(produto)
      produto.kardec = kardec.lastOrNull()?.saldo ?: 0
      produto.update()
      return kardec
    }
    val saldoKardec = ProdutoKardec.findKardec(produto)
    val saldoHoje = emptyList<ProdutoKardec>() //fetchKardecHoje (produto)
    return (saldoHoje + saldoKardec).ajustaOrdem()
  }

  private fun updateSaldoKardec(produto: ProdutoEstoque) {
    produto.dataUpdate = null
    val kardec = kardec(produto)
    produto.kardec = kardec.lastOrNull()?.saldo ?: 0
    produto.update()
  }

  fun updateKardec() = viewModel.exec {
    val produtos = subView.itensSelecionados()
    produtos.forEach { produto ->
      updateSaldoKardec(produto)
    }
    subView.reloadGrid()
  }

  fun updateProduto(bean: ProdutoEstoque?) {
    if(bean != null) {
      bean.update()
      updateView()
    }
  }

  fun copiaLocalizacao() = viewModel.exec {
    val itens = subView.itensSelecionados()
    if (itens.isEmpty()) fail("Nenhum item selecionado")

    val primeiro = itens.firstOrNull() ?: fail("Nenhum item selecionado")
    itens.forEach { item ->
      item.locApp = primeiro.locApp
      item.update()
    }
    updateView()
  }

  fun imprimeProdutos() = viewModel.exec {
    val produtos = subView.itensSelecionados()
    if (produtos.isEmpty()) {
      fail("Nenhum produto selecionado")
    }
    val filtro = subView.filtro()

    val report = PrintProdutosEstoque(filtro)

    report.print(
      dados = produtos, printer = subView.printerPreview(loja = 0)
    )
  }
}

interface ITabEstoqueSaldo : ITabView {
  fun filtro(): FiltroProdutoEstoque
  fun updateProduto(produtos: List<ProdutoEstoque>)
  fun updateKardec()
  fun itensSelecionados(): List<ProdutoEstoque>
  fun reloadGrid()
}
