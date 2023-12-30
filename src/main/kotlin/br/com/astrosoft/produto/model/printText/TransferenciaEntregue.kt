package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.PedidoTransf
import br.com.astrosoft.produto.model.beans.ProdutoPedidoTransf

class TransferenciaEntregue(val nota: PedidoTransf) : PrintText<ProdutoPedidoTransf>() {
  init {
    column(ProdutoPedidoTransf::codigoFormat, "Codigo", 6)
    column(ProdutoPedidoTransf::descricao, "Descricao", 40)
    column(ProdutoPedidoTransf::grade, "Grade", 9)
    column(ProdutoPedidoTransf::quantidade, "Quant", 6)
  }

  override fun printTitle(bean: ProdutoPedidoTransf) {
    writeln("Transferencia: ${nota.rota ?: "Rota nao definida"}", negrito = true)
    writeln(
      "Data: ${nota.data?.format() ?: "  /  /    "} Hora: ${nota.hora?.format() ?: "  :  "} Nota Transf: ${nota.notaTransf}",
      negrito = true
    )
    writeln("Usuario: ${nota.usuario ?: "Usuario nao definido"}", negrito = true)
    //println("Autorizado Por: ${nota.autorizado ?: "Autorizador nao definido"}", negrito = true)
    writeln("Referente: ${nota.referente ?: "Nao definido"}", negrito = true)
    //println("Entregue Por: ${nota.entregue ?: "Entregador nao definido"}", negrito = true)
    //println("Recebido Por: ${nota.recebido ?: "Recebedor nao definido"}", negrito = true)
    writeln("Vendedor (a): ${nota.nomeVendedor()}", negrito = true)
    writeln("Self Color: ${nota.selfColor ?: ""}", negrito = true)
    writeln("".padEnd(64, '-'))
  }

  override fun printSumary() {
    val entregueRelatorio = nota.entregueRelatorio()
    val lengthEntregue = entregueRelatorio.length
    val entregue = if (lengthEntregue >= 31) entregueRelatorio.substring(0, 31) else entregueRelatorio
    val campoRecebido = nota.recebidoRelatorio()
    val recebidoRelatorio = campoRecebido.value
    val lengthRecebido = recebidoRelatorio.length
    val recebido = if (lengthRecebido >= 31) (recebidoRelatorio).substring(0, 31) else recebidoRelatorio
    writeln("")
    writeln("DOCUMENTO NAO FISCAL", center = true)
    writeln("")
    writeln("")
    writeln("_______________________________", center = true)
    writeln(nota.sing, center = true)
    writeln("Autorizacao no Sistema", center = true)
    writeln("")
    writeln("")
    writeln("_______________________________".center(32) + "_______________________________".center(32))
    writeln("${entregue.center(32)}${recebido.center(32)}")
    writeln("Entregue".center(32) + campoRecebido.label.center(32))
    writeln("")
    writeln("")
  }
}