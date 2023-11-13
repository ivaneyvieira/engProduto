package br.com.astrosoft.produto.viewmodel.pedidoTransf

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrinterCups
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.ITabView
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.printText.RequisicaoTransferencia
import br.com.astrosoft.produto.model.zpl.EtiquetaChave
import java.time.LocalDate
import java.time.LocalTime

class TabPedidoTransfReservaViewModel(val viewModel: PedidoTransfViewModel) {
  fun updateView() {
    val filtro = subView.filtro(EMarcaPedido.CD)
    val pedidos = PedidoTransf.findTransf(filtro)
    subView.updatePedidos(pedidos)
  }

  fun findLoja(storeno: Int): Loja? {
    val lojas = Loja.allLojas()
    return lojas.firstOrNull { it.no == storeno }
  }

  fun findAllLojas(): List<Loja> {
    return Loja.allLojas()
  }

  fun marcaEnt() = viewModel.exec {
    val itens = subView.produtosSelcionados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    itens.forEach { produto ->
      produto.marca = EMarcaPedido.ENT.num
      val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
      val usuario = AppConfig.userLogin()?.login ?: ""
      produto.usuarioCD = "$usuario-$dataHora"
      produto.salva()
    }
    subView.updateProdutos()
  }

  fun marcaEntProdutos(codigoBarra: String) = viewModel.exec {
    val produto = subView.produtosCodigoBarras(codigoBarra) ?: fail("Produto não encontrado")
    produto.marca = EMarcaPedido.ENT.num
    val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
    val usuario = AppConfig.userLogin()?.login ?: ""
    produto.usuarioCD = "$usuario-$dataHora"
    subView.updateProduto(produto)
  }

  fun salvaProdutos() = viewModel.exec {
    val itens = subView.produtosMarcados()
    itens.ifEmpty {
      fail("Nenhum produto selecionado")
    }
    val dataHora = LocalDate.now().format() + "-" + LocalTime.now().format()
    val usuario = AppConfig.userLogin()?.login ?: ""
    itens.forEach { produto ->
      produto.usuarioCD = "$usuario-$dataHora"
      produto.salva()
      produto.expira()
    }
    imprimeEtiquetaEnt(itens)
    subView.updateProdutos()
  }

  private fun imprimeEtiquetaEnt(produto: List<ProdutoPedidoTransf>) {
    val user = AppConfig.userLogin() as? UserSaci
    user?.impressora?.let { impressora ->
      try {
        EtiquetaChave.printPreviewEnt(impressora, produto)
      } catch (e: Throwable) {
        e.printStackTrace()
        fail("Falha de impressão na impressora $impressora")
      }
    }
  }

  fun imprimePedido(pedido: PedidoTransf, impressora: String) = viewModel.exec {
    viewModel.view.showQuestion("Impressão do pedido na impressora $impressora") {
      val relatorio = RequisicaoTransferencia(pedido)
      relatorio.print(dados = pedido.produtos(), printer = PrinterCups(impressora))
    }
  }

  fun autorizaPedido(pedido: PedidoTransf, login: String, senha: String) = viewModel.exec {
    val user = UserSaci.findAll().firstOrNull { it.login.uppercase() == login.uppercase() && it.senha.uppercase() == senha.uppercase() }
    user ?: fail("Usuário ou senha inválidos")

    pedido.autoriza(user)
    updateView()
  }

  val subView
    get() = viewModel.view.tabPedidoTransfReserva
}

interface ITabPedidoTransfReserva : ITabView {
  fun filtro(marca: EMarcaPedido): FiltroPedidoTransf
  fun updatePedidos(pedidos: List<PedidoTransf>)
  fun updateProdutos()
  fun produtosSelcionados(): List<ProdutoPedidoTransf>
  fun produtosMarcados(): List<ProdutoPedidoTransf>
  fun produtosCodigoBarras(codigoBarra: String): ProdutoPedidoTransf?
  fun findPedido(): PedidoTransf?
  fun updateProduto(produto: ProdutoPedidoTransf)
}