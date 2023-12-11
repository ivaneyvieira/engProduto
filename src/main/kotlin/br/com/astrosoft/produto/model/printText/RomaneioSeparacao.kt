package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.produto.model.beans.ProdutoPedido

class RomaneioSeparacao: PrintText<ProdutoPedido>() {

  override fun printTitle(bean: ProdutoPedido) {
    val pedido = bean.pedido
    println("Romaneio de Separação de Reserva Retira: ${pedido.pedido}", negrito = true, center = true)

    println("Loja: ${pedido.nomeLoja}")
    println("Usuario da Impressao: ${pedido.username}")
  }
}