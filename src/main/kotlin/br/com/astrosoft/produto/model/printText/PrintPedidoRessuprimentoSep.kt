package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.model.beans.Ressuprimento
import kotlin.reflect.KProperty1

class PrintPedidoRessuprimentoSep(val pedido: Ressuprimento, propertyQuant: KProperty1<ProdutoRessuprimento, Int?>) :
  PrintText<ProdutoRessuprimento>() {
  override fun printTitle(bean: ProdutoRessuprimento) {
    val list = pedido.produtos()
    val quant = list.size
    val valorPedido = list.sumOf { it.vlPedido ?: 0.00 }
    writeln("Romaneio de Separacao do Ressuprimento da ${pedido.rotaRessuprimento}", negrito = true, center = true)
    writeln("")
    writeln(
      "Data: ${pedido.data.format()}   Pedido   : ${pedido.numero}   Valor R$ : ${valorPedido.format()}",
      negrito = true
    )
    val user = AppConfig.userLogin()
    writeln("Usuario: ${user?.login}", negrito = true)

    printLine()
  }

  init {
    column(ProdutoRessuprimento::codigo, "Codigo", 6)
    column(ProdutoRessuprimento::descricao, "Descricao", 30)
    column(ProdutoRessuprimento::grade, "Grade", 8)
    column(ProdutoRessuprimento::localizacao, "Loc", 4)
    column(ProdutoRessuprimento::estoque, "Estoq", 5)
    column(propertyQuant, "_Quant", 6)
  }

  override fun printSumary(bean: ProdutoRessuprimento?) {
    writeln("")
    writeln("")
  }
}