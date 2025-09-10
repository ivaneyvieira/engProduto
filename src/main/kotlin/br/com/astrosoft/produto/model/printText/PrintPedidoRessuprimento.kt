package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.PedidoRessuprimento
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import kotlin.reflect.KProperty1

class PrintPedidoRessuprimento(val pedido: PedidoRessuprimento, propertyQuant: KProperty1<ProdutoRessuprimento, Int?>) :
  PrintText<ProdutoRessuprimento>() {
  override fun printTitle(bean: ProdutoRessuprimento) {
    val list = pedido.produtos()
    val quant = list.size
    val valorPedido = list.sumOf { it.vlPedido ?: 0.00 }
    writeln("Romaneio de Separacao do Ressuprimento da ${pedido.rotaRessuprimento}", negrito = true, center = true)
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
    column(ProdutoRessuprimento::codigo, "Codigo", 6)
    column(ProdutoRessuprimento::descricao, "Descricao", 36 + 5)
    column(ProdutoRessuprimento::grade, "Grade", 8)
    //column(ProdutoRessuprimento::localizacao, "Loc", 4)
    column(propertyQuant, "_Quant", 6, lineBreak = true)
    column(ProdutoRessuprimento::vendnoRefRel, "", 40)
    column(ProdutoRessuprimento::localizacaoRel, "", 20)
  }

  override fun printSumary(bean: ProdutoRessuprimento?) {
    writeln("")
    writeln("")
  }
}