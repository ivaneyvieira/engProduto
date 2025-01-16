package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.rpad
import br.com.astrosoft.produto.model.beans.EEstoque
import br.com.astrosoft.produto.model.beans.FiltroProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import java.time.LocalDate
import java.time.LocalTime

class PrintProdutosEstoque(private val filtro: FiltroProdutoEstoque) : PrintText<ProdutoEstoque>() {
  private var valorPedido: Double = 0.0
  override fun printTitle(bean: ProdutoEstoque) {
    writeln("Relatorio Estoque", negrito = true, center = true)
    writeln("")
    writeln(
      "Data: ${LocalDate.now().format()}     Hora: ${LocalTime.now().format()}",
      negrito = true
    )
    writeln(
      text = "Loja: ${bean.lojaSigla}   Fornecedor: ${
        filtro.fornecedor.rpad(5, " ")
      }   Cl:${filtro.centroLucro.toString().rpad(5, " ")}",
      negrito = true
    )
    writeln(
      text = "Caracter: ${filtro.caracter.descricao}",
      negrito = true
    )
    val saldo = if (filtro.estoque == EEstoque.TODOS) "" else filtro.saldo.toString()
    writeln(
      text = "Estoque: ${filtro.estoque.descricao}  $saldo",
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
    column(ProdutoEstoque::descricao, "Descricao", 36)
    column(ProdutoEstoque::grade, "Grade", 8)
    column(ProdutoEstoque::locApp, "Loc", 4)
    column(ProdutoEstoque::saldo, "_Quant", 6)
  }

  override fun printSumary(bean: ProdutoEstoque?) {
    writeln("")
    writeln("")
    writeln("")
    writeln("")
  }
}