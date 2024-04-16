package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoPedido

class RomaneioSeparacaoL : PrintText<ProdutoPedido>() {
  override fun printTitle(bean: ProdutoPedido) {
    val pedido = bean.pedido
    val listObs = pedido?.listObs().orEmpty()
    writeln("Documento de Autorizacao de Retira Futura na ${pedido?.siglaLoja ?: ""}", negrito = true, center = true)

    //writeln("<B>Loja: </B>${pedido?.siglaLoja}")
    //writeln("<B>Usuario da Impressao: </B>${pedido?.userPrintName ?: AppConfig.userLogin()?.name ?: ""}")
    writeln("<B>NF de Fatura: </B>${pedido?.nfnoFat}/${pedido?.nfseFat}<B> Data: </B>${pedido?.dataFat}<B> Hora: </B>${pedido?.horaFat}")
    writeln("<B>PDV: </B>${pedido?.pdvnoVenda}<B> Pgto: </B>${pedido?.metodo ?: ""}<B> Valor: </B>${pedido?.valorFat.format()}")
    writeln("<B>Cliente: </B>${pedido?.cliente}")
    writeln("<B>Vendedor (a): </B>${pedido?.vendedor}")
    if (listObs.isNotEmpty()) {
      writeln("<B>Obs na Reserva: </B>${listObs.firstOrNull() ?: ""}")
    } else {
      writeln("<B>Obs na Reserva: </B>")
    }
    if (listObs.size > 1) {
      listObs.subList(1, listObs.size).forEach { obs ->
        writeln(obs)
      }
    }
    printLine()
  }

  init {
    column(ProdutoPedido::codigo, "Codigo", 6)
    column(ProdutoPedido::descricao, "Descricao", 40)
    column(ProdutoPedido::grade, "Grade", 9)
    column(ProdutoPedido::qtd, "Quant", 6)
  }

  override fun printSumary(bean: ProdutoPedido?) {
    writeln("")
    writeln("DOCUMENTO NAO FISCAL", center = true)
    writeln("")
    writeln("")

    val pedido = bean?.pedido

    val usuario = pedido?.userPrintName ?: AppConfig.userLogin()?.name ?: ""

    writeln("________________________________________________", center = true)
    writeln(usuario, center = true)
    writeln("Autorizacao", center = true)
  }
}