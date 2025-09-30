package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.PedidoTransf
import br.com.astrosoft.produto.model.beans.ProdutoPedidoTransf
import br.com.astrosoft.produto.model.saci

class RequisicaoTransferenciaConsulta(val nota: PedidoTransf) : PrintText<ProdutoPedidoTransf>() {
  init {
    val rota = nota.rotaPedido()
    val listLoja = saci.allLojas()
    val lojaOrigem = listLoja.firstOrNull { it.no == nota.lojaNoOri }?.sname ?: ""
    val lojaDestino = listLoja.firstOrNull { it.no == nota.lojaNoDes }?.sname ?: ""
    column(ProdutoPedidoTransf::codigoFormat, "Codigo", 6)
    column(ProdutoPedidoTransf::descricao, "Descricao", 27)
    column(ProdutoPedidoTransf::grade, "Grade", 8)
    column(ProdutoPedidoTransf::saldoO, "Qt_$lojaOrigem", 6)
    column(ProdutoPedidoTransf::saldoD, "Qt_$lojaDestino", 6)
    column(ProdutoPedidoTransf::quantidade, "Qt_Ped", 6)
  }


  override fun printTitle(bean: ProdutoPedidoTransf) {
    writeln("Requisicao de Transferencia: ${nota.rota ?: "Rota nao definida"}", negrito = true)
    writeln(
      "Data: ${nota.data?.format() ?: "  /  /    "} Hora: ${nota.hora?.format() ?: "  :  "} Reserva: ${nota.ordno}",
      negrito = true
    )
    writeln("Usuario: ${nota.usuario ?: "Usuario nao definido"}", negrito = true)
    writeln("Referente: ${nota.referente ?: "Nao definido"}", negrito = true)
    writeln("Vendedor (a): ${nota.nomeVendedor()}", negrito = true)
    writeln("Self Color: ${nota.selfColor ?: ""}", negrito = true)
    writeln("".padEnd(64, '-'))
  }

  override fun printSumary(bean: ProdutoPedidoTransf?) {
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