package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento

class PrintRessuprimento : PrintText<ProdutoRessuprimento>() {
  override fun printTitle(bean: ProdutoRessuprimento) {
    val pedido = bean.ordno
    writeln("Pedido ${pedido}", negrito = true, center = true)

    printLine()
  }

  init {
    column(ProdutoRessuprimento::codigo, "Codigo", 6)
    column(ProdutoRessuprimento::descricao, "Descricao", 35)
    column(ProdutoRessuprimento::grade, "Grade", 9)
    column(ProdutoRessuprimento::localizacao, "Loc", 4)
    column(ProdutoRessuprimento::quantidade, "Quant", 6)
  }

  override fun printSumary() {
    writeln("")
    writeln("DOCUMENTO NAO FISCAL", center = true)
    writeln("")
    writeln("")
  }
}