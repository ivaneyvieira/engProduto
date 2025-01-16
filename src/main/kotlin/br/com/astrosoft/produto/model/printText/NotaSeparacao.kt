package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.NotaSaidaProduto

class NotaSeparacao(val rotas: List<String>, val userList: List<String>) : PrintText<NotaSaidaProduto>() {
  override fun printTitle(bean: NotaSaidaProduto) {
    writeln("Roteiro de Entrega CD5A", negrito = true, center = true)

    printLine()

    val rotasStr = rotas.joinToString(" / ")

    writeln("Rota: $rotasStr", negrito = true, center = true, expand = true)

    printLine()

    writeln("Motorista: ${bean.motorista}      Data Entrega: ${bean.dataEntrega.format()}")

    val userPrint = userList.joinToString(" / ")

    writeln("Usuario da Impressao: $userPrint")

    printLine()
  }

  init {
    column(NotaSaidaProduto::codigo, "Codigo", 6)
    column(NotaSaidaProduto::descricao, "Descricao", 41)
    column(NotaSaidaProduto::grade, "Grade", 8)
    column(NotaSaidaProduto::quantidade, "Qtd", 6)
  }

  override fun groupBotton(beanDetail: NotaSaidaProduto): String {
    return "Lj:${beanDetail.loja} Ped:${beanDetail.pedido} NF:${beanDetail.nota} Dt:${beanDetail.data.format("dd/MM/yy")} Cli:${beanDetail.cliente} Vl:${beanDetail.valorNota}"
  }

  override fun printSumary(bean: NotaSaidaProduto?) {
    writeln("")
    writeln("DOCUMENTO NAO FISCAL", center = true)
    writeln("")
    writeln("")
  }
}
