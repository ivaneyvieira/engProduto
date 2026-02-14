package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ProdutoMovimentacao

class PrintReposicaoMovimentacao() : PrintText<ProdutoMovimentacao>() {
  private var valorPedido: Double = 0.0
  override fun printTitle(bean: ProdutoMovimentacao) {
    writeln("Reposicao Loja", negrito = true, center = true)
    writeln("")
    writeln(
      "Data: ${bean.data.format()}   Pedido   : ${bean.numero}",
      negrito = true
    )
    val user = AppConfig.userLogin()
    writeln("Usuario: ${user?.name}", negrito = true)

    printLine()
  }

  override fun print(dados: List<ProdutoMovimentacao>, printer: IPrinter) {
    valorPedido = dados.sumOf { ((it.movimentacao ?: 0) * 1.00) }
    super.print(dados, printer)
  }

  init {
    column(ProdutoMovimentacao::codigo, "Codigo", 6)
    column(ProdutoMovimentacao::descricao, "Descricao", 36)
    column(ProdutoMovimentacao::grade, "Grade", 8)
    column(ProdutoMovimentacao::locApp, "Loc", 4)
    column(ProdutoMovimentacao::movimentacao, "_Quant", 6)
  }

  override fun printSumary(bean: ProdutoMovimentacao?) {
    writeln("")
    writeln("")
    writeln("____________________________________", center = true)
    //writeln(bean?.recebidoNome ?: "", center = true)
    writeln("")
    writeln("Separado/Entregue", center = true)
    writeln("")
    writeln("")
    writeln("____________________________________", center = true)
    //writeln(bean?.entregueNome ?: "", center = true)
    writeln("")
    writeln("Recebido", center = true)
    writeln("")
    writeln("")
  }
}