package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto
import java.time.LocalDate
import java.time.LocalTime

class PrintProdutosConferenciaEstoque(val titulo: String) : PrintText<ProdutoEstoqueAcerto>() {
  override fun printTitle(bean: ProdutoEstoqueAcerto) {
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
    column(ProdutoEstoqueAcerto::codigo, "Codigo", 6)
    column(ProdutoEstoqueAcerto::descricao, "Descricao", 34)
    column(ProdutoEstoqueAcerto::grade, "Grade", 8)
    column(ProdutoEstoqueAcerto::locApp, "Loc", 4)
    column(ProdutoEstoqueAcerto::estoqueSis, "___Quant", 8, lineBreak = true)
    column(ProdutoEstoqueAcerto::saldoBarraRef, "", 47)
  }

  override fun printSumary(bean: ProdutoEstoqueAcerto?) {
    writeln("")
    writeln("")
    writeln("")
    writeln("")
  }
}