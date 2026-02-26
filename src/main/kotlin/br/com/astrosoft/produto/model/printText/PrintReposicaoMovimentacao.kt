package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ERota
import br.com.astrosoft.produto.model.beans.ProdutoMovimentacao

class PrintReposicaoMovimentacao() : PrintText<ProdutoMovimentacao>() {
  private var valorPedido: Double = 0.0
  override fun printTitle(bean: ProdutoMovimentacao) {
    val rota = bean.noRota?.let { noRota ->
      ERota.entries.firstOrNull { it.numero == noRota }
    }

    val rotaDescricao = rota?.descricao ?: ""


    writeln("Reposicao Rota $rotaDescricao", negrito = true, center = true)
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
    val entregueNome = bean?.entregueNome ?: ""
    val recebidoNome = bean?.recebidoNome ?: ""
    writeln("")
    writeln("")
    writeln("____________________________________", center = true)
    if (entregueNome.isNotBlank()) {
      writeln(entregueNome ?: "", center = true)
    }
    writeln("Separado/Entregue", center = true)
    writeln("${bean?.dataEntrege.format()} - ${bean?.horaEntrege.format("HH:mm")}", center = true)
    writeln("")
    writeln("")
    writeln("____________________________________", center = true)
    if (recebidoNome.isNotBlank()) {
      writeln(recebidoNome ?: "", center = true)
    }
    writeln("Recebido", center = true)
    writeln("${bean?.dataRecebido.format()} - ${bean?.horaRecebido.format("HH:mm")}", center = true)
    writeln("")
    writeln("")
  }
}