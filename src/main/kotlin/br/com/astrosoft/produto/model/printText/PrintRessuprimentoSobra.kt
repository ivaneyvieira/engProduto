package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimentoSobra
import br.com.astrosoft.produto.model.beans.Ressuprimento
import kotlin.reflect.KProperty1

class PrintRessuprimentoSobra(val pedido: Ressuprimento, val ressuprimentoTitle: String) :
  PrintText<ProdutoRessuprimentoSobra>() {
  override fun printTitle(bean: ProdutoRessuprimentoSobra) {
    writeln("Divergência no Recebimento do Ressuprimento da $ressuprimentoTitle", negrito = true, center = true)

    writeln("Usuario: ${pedido.usuarioApp}", negrito = true)

    printLine()
  }

  override fun groupBotton(beanDetail: ProdutoRessuprimentoSobra): String {
    return beanDetail.grupo
  }

  init {
    column(ProdutoRessuprimentoSobra::codigo, "Codigo", 6)
    column(ProdutoRessuprimentoSobra::descricao, "Descricao", 36)
    column(ProdutoRessuprimentoSobra::grade, "Grade", 8)
    column(ProdutoRessuprimentoSobra::localizacao, "Loc", 4)
    column(ProdutoRessuprimentoSobra::quantidade, "_Quant", 6)
  }

  override fun printSumary() {
    writeln("")
    writeln("")
    writeln("____________________________________", center = true)
    writeln("${pedido.entreguePor}", center = true)
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