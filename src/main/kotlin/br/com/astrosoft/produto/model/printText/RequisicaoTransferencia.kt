package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.PedidoTransf
import br.com.astrosoft.produto.model.beans.ProdutoPedidoTransf

class RequisicaoTransferencia(val nota: PedidoTransf) : PrintText<ProdutoPedidoTransf>() {
  init {
    column(ProdutoPedidoTransf::codigoFormat, "Codigo", 6)
    column(ProdutoPedidoTransf::descricao, "Descricao", 30)
    column(ProdutoPedidoTransf::grade, "Grade", 9)
    column(ProdutoPedidoTransf::quantidade, "Quant", 6)
  }

  override fun titleLines(bean: ProdutoPedidoTransf): List<String> {
    return listOf(
      "Requisicao de Transferencia: ${nota.rota ?: "Rota nao definida"}",
      "Data: ${nota.data?.format() ?: "  /  /    "} Hora: ${nota.hora?.format() ?: "  :  "} Reserva: ${nota.ordno}",
      "Usuario: ${nota.usuario ?: "Usuario nao definido"}",
      "Autorizado Por: ${nota.autorizado ?: "Autorizador nao definido"}",
      "Referente: ${nota.referente ?: "Nao definido"}",
      "Entregue Por: ${nota.entregue ?: "Entregador nao definido"}",
      "Recebido Por: ${nota.recebido ?: "Recebedor nao definido"}"
    )
  }
}