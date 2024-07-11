package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.ProdutoPedido

class NotaSeparacao(val rotas: List<String>) : PrintText<NotaSaida>() {
  override fun printTitle(bean: NotaSaida) {
    writeln("Roteiro de Entrega CD5A", negrito = true, center = true)

    printLine()

    val rotasStr = rotas.joinToString(" / ")

    writeln("Rota: $rotasStr", negrito = true, center = true, expand = true)

    printLine()

    writeln("Motorista: ${bean.nomeMotorista}      Data Entrega: ${bean.dataEntrega.format()}")

    printLine()
  }

  init {
    column(NotaSaida::loja, "Loja", 4)
    column(NotaSaida::pedido, "Pedido", 8)
    column(NotaSaida::nota, "NF", 10)
    column(NotaSaida::dataStr, "Data", 10)
    column(NotaSaida::cliente, "Cliente", 7)
    column(NotaSaida::valorNota, "Valor", 15)
  }

  override fun printSumary(bean: NotaSaida?) {
    writeln("")
    writeln("DOCUMENTO NAO FISCAL", center = true)
    writeln("")
    writeln("")
  }
}