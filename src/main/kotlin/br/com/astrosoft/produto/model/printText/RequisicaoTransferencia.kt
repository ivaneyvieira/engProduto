package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.viewmodel.PrintText
import br.com.astrosoft.produto.model.beans.PedidoTransf
import br.com.astrosoft.produto.model.beans.ProdutoPedidoTransf

class RequisicaoTransferencia(val nota : PedidoTransf) : PrintText<ProdutoPedidoTransf>() {
  init {
    columText("Codigo", 6) {codigo.lpad(6, "0")}
    columText("Descricao", 30) {descricao ?: ""}
    columText("Grade", 9) {grade ?: ""}
    columNumber("Quant", 6, format = "0") { quantidade * 1.00 }
  }
  override fun titleLines(bean : ProdutoPedidoTransf): List<String> {
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