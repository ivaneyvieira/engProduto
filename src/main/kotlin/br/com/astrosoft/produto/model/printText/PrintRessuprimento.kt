package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.model.beans.Ressuprimento
import kotlin.reflect.KProperty1

class PrintRessuprimento(
  val pedido: Ressuprimento,
  val quant: Int,
  propertyQuant: KProperty1<ProdutoRessuprimento, Int?>,
) : PrintText<ProdutoRessuprimento>() {
  override fun printTitle(bean: ProdutoRessuprimento) {
    writeln("Romaneio de Separacao do Ressuprimento da ${pedido.rotaRessuprimento}", negrito = true, center = true)
    writeln("")
    writeln(
      "Data: ${pedido.data.format()}   Pedido   : ${pedido.numero}   Valor R$ : ${pedido.valorNota.format()}",
      negrito = true
    )
    writeln("Data: ${pedido.dataBaixa.format()}   NF Transf: ${pedido.notaBaixa}    Qnt Itens: $quant", negrito = true)
    writeln("Usuario: ${pedido.usuarioApp}", negrito = true)

    printLine()
  }

  init {
    column(ProdutoRessuprimento::codigo, "Codigo", 6)
    column(ProdutoRessuprimento::descricao, "Descricao", 36)
    column(ProdutoRessuprimento::grade, "Grade", 8)
    column(ProdutoRessuprimento::localizacao, "Loc", 4)
    column(propertyQuant, "_Quant", 6)
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