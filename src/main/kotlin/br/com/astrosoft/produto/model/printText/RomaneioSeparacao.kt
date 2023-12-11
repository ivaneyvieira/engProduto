package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoPedido

class RomaneioSeparacao : PrintText<ProdutoPedido>() {
  override fun printTitle(bean: ProdutoPedido) {
    val pedido = bean.pedido
    val listObs = pedido?.listObs().orEmpty()
    println("Romaneio de Separacao para Reteria: Reserva ${pedido?.pedido}", negrito = true, center = true)

    println("<B>Loja: </B>${pedido?.nomeLoja}")
    println("<B>Usuario da Impressao: </B>${AppConfig.userLogin()?.login}")
    println("<B>NF de Fatura: </B>${pedido?.nfnoFat}/${pedido?.nfseFat}<B> Data: </B>${pedido?.dataFat}<B> Hora: </B>${pedido?.horaFat}")
    println("<B>PDF: </B>${pedido?.pdvnoVenda}<B> Pgto: </B>${pedido?.metodo ?: ""}<B> Valor: </B>${pedido?.valorFat.format()}")
    println("<B>Cliente: </B>${pedido?.cliente}")
    println("<B>Vendedor (a): </B>${pedido?.vendedor}")
    if (listObs.isNotEmpty()) {
      println("<B>Obs na Reserva: </B>${listObs.firstOrNull() ?: ""}")
    }else{
      println("<B>Obs na Reserva: </B>")
    }
    if (listObs.size > 1) {
      listObs.subList(1, listObs.size).forEach { obs ->
        println("                $obs")
      }
    }
  }

  init {
    column(ProdutoPedido::codigo, "Codigo", 6)
    column(ProdutoPedido::descricao, "Descricao", 40)
    column(ProdutoPedido::grade, "Grade", 9)
    column(ProdutoPedido::qtd, "Quant", 6)
  }

  override fun printSumary() {
    println("")
    println("DOCUMENTO NAO FISCAL", center = true)
    println("")
    println("")
  }
}