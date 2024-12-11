package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.PedidoAcerto
import br.com.astrosoft.produto.model.beans.ProdutoAcerto
import kotlin.reflect.KProperty1

class PrintPedidoAcerto(val pedido: PedidoAcerto, propertyQuant: KProperty1<ProdutoAcerto, Int?>) :
  PrintText<ProdutoAcerto>() {
  override fun printTitle(bean: ProdutoAcerto) {
    val list = pedido.produtos()
    val quant = list.size
    val valorPedido = pedido.totalPedido ?: 0.00
    writeln("Romaneio de Separacao do Acerto da ${pedido.rotaAcerto}", negrito = true, center = true)
    writeln("")
    writeln(
      "Data: ${pedido.data.format()}   Pedido   : ${pedido.pedido}   Valor R$ : ${valorPedido.format()}",
      negrito = true
    )
    val user = AppConfig.userLogin()
    writeln("Usuario: ${user?.login}", negrito = true)

    printLine()
  }

  init {
    column(ProdutoAcerto::codigo, "Codigo", 6)
    column(ProdutoAcerto::descricao, "Descricao", 36)
    column(ProdutoAcerto::grade, "Grade", 8)
    column(ProdutoAcerto::localizacao, "Loc", 4)
    column(propertyQuant, "_Quant", 6)
  }

  override fun printSumary(bean: ProdutoAcerto?) {
    writeln("")
    writeln("")
  }
}