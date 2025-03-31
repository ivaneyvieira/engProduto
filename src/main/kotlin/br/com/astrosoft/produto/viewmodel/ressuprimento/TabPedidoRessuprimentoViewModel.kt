package br.com.astrosoft.produto.viewmodel.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoRessuprimento
import br.com.astrosoft.produto.model.printText.PrintPedidoRessuprimento
import br.com.astrosoft.produto.model.saci
import com.vaadin.flow.component.ClickEvent

class TabPedidoRessuprimentoViewModel(val viewModel: RessuprimentoViewModel) {
  val subView
    get() = viewModel.view.tabPedidoRessuprimento

  fun updateView() {
    LocalizacaoAlternativa.update()
    val filtro = subView.filtro()
    val user = AppConfig.userLogin() as? UserSaci
    val pedidos = PedidoRessuprimento.findPedidoRessuprimento(filtro).filter { pedido ->
      if (user?.ressuprimentoExibePedidoPai == true) {
        true
      } else {
        pedido.pedido?.toString()?.length != 5
      }
    }
    subView.updatePedidos(pedidos)
  }

  fun imprimePedido() = viewModel.exec {
  }

  fun separaPedido() = viewModel.exec {
    val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) fail("Nenhum pedido selecionado")

    viewModel.view.showQuestion("Confirma a separação do pedido?") {
      var ordnoNovo: Int = produtos.firstOrNull()?.pedidoNovo()?.ordno ?: 0
      produtos.forEach { produto ->
        produto.separaPedido(ordnoNovo)
      }

      if (ordnoNovo == 0) {
        viewModel.view.showInformation("Não foi gerado o pedido")
      } else {
        viewModel.view.showInformation("Foi gerado o pedido número $ordnoNovo")
      }
      subView.updateProdutos()
      updateView()
    }
  }

  fun duplicaPedido() = viewModel.exec {
    val pedidos = subView.pedidoSelecionado()
    if (pedidos.isEmpty()) fail("Nenhum pedido selecionado")
    if (pedidos.size > 1) fail("Selecione apenas um pedido para duplicar")
    val pedido = pedidos.first()

    subView.confirmaLogin("Confirma a duplicação do pedido?", UserSaci::ressuprimentoDuplica) {
      val pedidoNovo = pedido.duplicaPedido()

      if (pedidoNovo == null) {
        viewModel.view.showInformation("Não foi gerado o pedido")
      } else {
        viewModel.view.showInformation("Foi gerado o pedido número ${pedidoNovo.ordno}")
      }

      updateView()
    }
  }

  fun removePedido() = viewModel.exec {
    val pedidos = subView.pedidoSelecionado()
    if (pedidos.isEmpty()) fail("Nenhum pedido selecionado")

    subView.confirmaLogin("Confirma a remoção do pedido?", UserSaci::ressuprimentoRemove) {
      pedidos.forEach { pedido ->
        pedido.removerPedido()
      }
      updateView()
    }
  }

  fun previewPedido(ressuprimento: PedidoRessuprimento) {
    val produtos = ressuprimento.produtos()

    val relatorio = PrintPedidoRessuprimento(ressuprimento, ProdutoRessuprimento::qtPedido)

    relatorio.print(
      dados = produtos.sortedWith(
        compareBy(
          ProdutoRessuprimento::descricao,
          ProdutoRessuprimento::codigo,
          ProdutoRessuprimento::grade
        )
      ),
      printer = subView.printerPreview(loja = 1)
    )
  }

  fun saveProduto(ressuprimento: ProdutoRessuprimento) = viewModel.exec {
    val valor = ressuprimento.qtPedido ?: 0
    val valorMax = ressuprimento.qttyMax
    val valorMin = ressuprimento.qttyMin

    if (valor >= valorMin && valor <= valorMax) {
      ressuprimento.salvaQuantidade()
      subView.updateProdutos()
    } else {
      subView.updateProdutos()
      fail("A quantidade deveria está entre $valorMin e $valorMax")
    }
  }

  fun removeProduto() {
    val produtos = subView.produtosSelecionados()
    if (produtos.isEmpty()) fail("Nenhum produto selecionado")

    subView.confirmaLogin("Confirma a remoção do produto?", UserSaci::ressuprimentoRemoveProd) {
      produtos.forEach { produto ->
        produto.removerProduto()
      }
      subView.updateProdutos()
    }
  }

  fun geraPlanilha(): ByteArray = viewModel.exec {
    val pedidos = subView.pedidoSelecionado().ifEmpty {
      fail("Nenhum pedido selecionado")
    }
    val produtos = pedidos.flatMap {
      it.produtos()
    }
    val planilha = PlanilhaProdutoRessuprimento()
    planilha.write(produtos)
  }

  fun copiaPedido()=  viewModel.exec{
    subView.formCopiaPedido{beanCopia : BeanCopia ->
      saci.copiaPedido(beanCopia)
    }
  }
}

interface ITabPedidoRessuprimento : ITabView {
  fun filtro(): FiltroPedidoRessuprimento
  fun updatePedidos(pedido: List<PedidoRessuprimento>)
  fun pedidoSelecionado(): List<PedidoRessuprimento>
  fun produtosSelecionados(): List<ProdutoRessuprimento>
  fun updateProdutos()
  fun confirmaLogin(msg: String, permissao: UserSaci.() -> Boolean, onLogin: () -> Unit)
  fun formCopiaPedido(block:   (beanCopia : BeanCopia) -> Unit)
}
