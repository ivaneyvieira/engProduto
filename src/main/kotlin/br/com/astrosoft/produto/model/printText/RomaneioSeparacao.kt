package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoPedido

class RomaneioSeparacao : PrintText<ProdutoPedido>() {
  override fun printTitle(bean: ProdutoPedido) {
    val pedido = bean.pedido
    println("Romaneio de Separacao para Reteria: Reserva ${pedido?.pedido}", negrito = true, center = true)

    println("Loja: ${pedido?.nomeLoja}")
    println("Usuario da Impressao: ${AppConfig.userLogin()?.login}")
    println("NF de Fatura: ${pedido?.nfnoFat}/${pedido?.nfseFat} Data: ${pedido?.dataFat} Hora: ${pedido?.horaFat}")
    println("PDF: ${pedido?.pdvnoVenda} Pgto: ${pedido?.metodo ?: ""} Valor: ${pedido?.valorFat.format()}")
    println("Cliente: ${pedido?.cliente}")
    println("Vendedor (a): ${pedido?.vendedor}")
    println("Obs na Reserva: ${pedido?.observacao ?: ""}")
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