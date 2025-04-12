package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueGarantia
import java.time.LocalDate
import java.time.LocalTime

class PrintProdutosConferenciaGarantia(val titulo: String) : PrintText<ProdutoEstoqueGarantia>() {
  override fun printTitle(bean: ProdutoEstoqueGarantia) {
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
    column(ProdutoEstoqueGarantia::codigo, "Codigo", 6)
    column(ProdutoEstoqueGarantia::descricao, "Descricao", 34)
    column(ProdutoEstoqueGarantia::grade, "Grade", 8)
    column(ProdutoEstoqueGarantia::locApp, "Loc", 4)
    column(ProdutoEstoqueGarantia::estoqueSis, "___Quant", 8, lineBreak = true)
    column(ProdutoEstoqueGarantia::saldoBarraRef, "", 47)
  }

  override fun printSumary(bean: ProdutoEstoqueGarantia?) {
    writeln("")
    writeln("")
    writeln("")
    writeln("")
  }
}