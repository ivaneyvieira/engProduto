package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.PedidoTransf
import br.com.astrosoft.produto.model.beans.ProdutoPedidoTransf

class RequisicaoTransferencia(val nota: PedidoTransf) : PrintText<ProdutoPedidoTransf>() {
  init {
    column(ProdutoPedidoTransf::codigoFormat, "Codigo", 6)
    column(ProdutoPedidoTransf::descricao, "Descricao", 40)
    column(ProdutoPedidoTransf::grade, "Grade", 9)
    column(ProdutoPedidoTransf::quantidade, "Quant", 6)
  }

  override fun printTitle(bean: ProdutoPedidoTransf) {
    println("Requisicao de Transferencia: ${nota.rota ?: "Rota nao definida"}", negrito = true)
    println(
      "Data: ${nota.data?.format() ?: "  /  /    "} Hora: ${nota.hora?.format() ?: "  :  "} Reserva: ${nota.ordno}",
      negrito = true
    )
    println("Usuario: ${nota.usuario ?: "Usuario nao definido"}", negrito = true)
    //println("Autorizado Por: ${nota.autorizado ?: "Autorizador nao definido"}", negrito = true)
    println("Referente: ${nota.referente ?: "Nao definido"}", negrito = true)
    //println("Entregue Por: ${nota.entregue ?: "Entregador nao definido"}", negrito = true)
    //println("Recebido Por: ${nota.recebido ?: "Recebedor nao definido"}", negrito = true)
    println("Self Color: ${nota.selfColor ?: ""}", negrito = true)
    println("".padEnd(64, '-'))
  }

  override fun printSumary() {
    val lengthEntregue = (nota.entregue ?: "").length
    val entregue = if (lengthEntregue >= 31) (nota.entregue ?: "").substring(0, 31) else nota.entregue ?: ""
    val margemEntregue = (31 - lengthEntregue) / 2
    val lengthRecebido = (nota.recebido ?: "").length
    val recebido = if (lengthRecebido >= 31) (nota.recebido ?: "").substring(0, 31) else nota.recebido ?: ""
    val margemRecebido = (31 - lengthRecebido) / 2
    println("")
    println("DOCUMENTO NAO FISCAL", center = true)
    println("")
    println("")
    println("_______________________________", center = true)
    println(nota.sing, center = true)
    println("Autorizacao no Sistema", center = true)
    println("")
    println("")
    println("_______________________________  _______________________________")
    println("${" ".repeat(margemEntregue)}$entregue${" ".repeat(margemEntregue)}  ${" ".repeat(margemRecebido)}$recebido")
    println("            Entregue                        Recebido")
    println("")
    println("")
  }
}