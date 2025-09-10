package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.EMetodo
import br.com.astrosoft.produto.model.beans.ReposicaoProduto

class PrintReposicao() :
  PrintText<ReposicaoProduto>() {
  private var valorPedido: Double = 0.0
  override fun printTitle(bean: ReposicaoProduto) {
    writeln("Retorno Loja", negrito = true, center = true)
    writeln("")
    writeln(
      "Data: ${bean.data.format()}   Pedido   : ${bean.numero}",
      negrito = true
    )
    val user = AppConfig.userLogin()
    writeln("Usuario: ${user?.name}", negrito = true)

    printLine()
  }

  override fun print(dados: List<ReposicaoProduto>, printer: IPrinter) {
    valorPedido = dados.sumOf { ((it.quantidade ?: 0) * 1.00) }
    super.print(dados, printer)
  }

  init {
    column(ReposicaoProduto::codigo, "Codigo", 6)
    column(ReposicaoProduto::descricao, "Descricao", 36)
    column(ReposicaoProduto::grade, "Grade", 8)
    column(ReposicaoProduto::localizacao, "Loc", 4)
    column(ReposicaoProduto::quantidade, "_Quant", 6)
  }

  override fun printSumary(bean: ReposicaoProduto?) {
    val titlulo01 = if (bean?.metodo == EMetodo.RETORNO.num) {
      "Entregue"
    } else {
      "Separado/Entregue"
    }
    val titlulo02 = if (bean?.metodo == EMetodo.RETORNO.num) {
      "Recebido"
    } else {
      "Recebido"
    }
    writeln("")
    writeln("")
    writeln("____________________________________", center = true)
    writeln(bean?.entregueNomeAjuste ?: "", center = true)
    writeln(titlulo01, center = true)
    writeln("")
    writeln("")
    writeln("____________________________________", center = true)
    writeln(bean?.recebidoNomeAjuste ?: "", center = true)
    writeln(titlulo02, center = true)
    writeln("")
    writeln("")
  }
}