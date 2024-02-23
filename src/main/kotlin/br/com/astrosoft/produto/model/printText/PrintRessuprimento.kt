package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.model.beans.Ressuprimento

class PrintRessuprimento(val pedido: Ressuprimento) : PrintText<ProdutoRessuprimento>() {
  override fun printTitle(bean: ProdutoRessuprimento) {
    writeln("Romaneio de Separacao do Ressuprimento da Rota45", negrito = true, center = true)
    writeln("")
    writeln("Data: ${pedido.data.format()}        Pedido   : ${pedido.numero}", negrito = true)
    writeln("Data: ${pedido.dataBaixa.format()}        NF Transf: ${pedido.notaBaixa}", negrito = true)
    writeln("Usuario: ${pedido.usuario}", negrito = true)

    printLine()
  }

  init {
    column(ProdutoRessuprimento::codigo, "Codigo", 6)
    column(ProdutoRessuprimento::descricao, "Descricao", 36)
    column(ProdutoRessuprimento::grade, "Grade", 8)
    column(ProdutoRessuprimento::localizacao, "Loc", 4)
    column(ProdutoRessuprimento::quantidade, "_Quant", 6)
  }

  override fun printSumary() {
    writeln("")
    writeln("")
    writeln("____________________________________", center = true)
    writeln("${pedido.sing}", center = true)
    writeln("Separado/Entregue", center = true)
    writeln("")
    writeln("")
    writeln("_______________________________  _______________________________", center = true)
    writeln("${pedido.transportadoPor.center(32)}${pedido.recebidoPor.center(32)}", center = true)
    writeln("${"Tranportado".center(32)}${"Recebido".center(32)}")
    writeln("")
    writeln("")
  }
}