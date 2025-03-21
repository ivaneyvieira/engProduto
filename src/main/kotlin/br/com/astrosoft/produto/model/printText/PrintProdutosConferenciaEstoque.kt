package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import java.time.LocalDate
import java.time.LocalTime

class PrintProdutosConferenciaEstoque(val titulo: String) : PrintText<ProdutoEstoque>() {
  private var valorPedido: Double = 0.0
  override fun printTitle(bean: ProdutoEstoque) {
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

  override fun print(dados: List<ProdutoEstoque>, printer: IPrinter) {
    valorPedido = dados.sumOf { ((it.estoque ?: 0) * 1.00) }
    super.print(dados, printer)
  }

  init {
    column(ProdutoEstoque::codigoStr, "Codigo", 6)
    column(ProdutoEstoque::descricao, "Descricao", 34)
    column(ProdutoEstoque::grade, "Grade", 8)
    column(ProdutoEstoque::locApp, "Loc", 4)
    column(ProdutoEstoque::saldo, "___Quant", 8, lineBreak = true)
    column(ProdutoEstoque::saldoBarraRef, "", 47)
  }

  override fun printSumary(bean: ProdutoEstoque?) {
    writeln("")
    writeln("")
    writeln("")
    writeln("")
  }
}