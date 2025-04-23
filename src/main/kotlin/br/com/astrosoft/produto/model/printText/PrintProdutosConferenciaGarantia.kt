package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoPedidoGarantia
import java.time.LocalDate
import java.time.LocalTime

class PrintProdutosConferenciaGarantia(val titulo: String) : PrintText<ProdutoPedidoGarantia>() {
  override fun printTitle(bean: ProdutoPedidoGarantia) {
    writeln(titulo, negrito = true, center = true)
    writeln("")
    writeln(
      "Loja: ${bean.lojaSigla}     Data: ${LocalDate.now().format()}     Hora: ${LocalTime.now().format()}",
      negrito = true
    )
    writeln(
      text = "Usuario: ${AppConfig.userLogin()?.name}",
      negrito = true
    )

    printLine()
  }

  init {
    column(ProdutoPedidoGarantia::codigo, "Codigo", 6)
    column(ProdutoPedidoGarantia::descricao, "Descricao", 34)
    column(ProdutoPedidoGarantia::grade, "Grade", 8)
    column(ProdutoPedidoGarantia::locApp, "Loc", 4)
    column(ProdutoPedidoGarantia::estoqueDev, "___Quant", 8, lineBreak = true)
    column(ProdutoPedidoGarantia::saldoBarraRef, "", 47)
  }

  override fun printSumary(bean: ProdutoPedidoGarantia?) {
    writeln("")
    writeln("")
    writeln("")
    writeln("")
  }
}