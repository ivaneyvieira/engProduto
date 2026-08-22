package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.ERota
import br.com.astrosoft.produto.model.beans.ProdutoMovimentacao
import java.time.LocalTime

class PrintReposicaoConferencia() : PrintText<ProdutoMovimentacao>() {
  private var valorPedido: Double = 0.0
  override fun printTitle(bean: ProdutoMovimentacao) {
    val rota = bean.noRota?.let { noRota ->
      ERota.entries.firstOrNull { it.numero == noRota }
    }

    val rotaDescricao = rota?.descricao ?: ""

    writeln("Separacao Rota $rotaDescricao", negrito = true, center = true)
    writeln("")
    val text1 = "Data: ${bean.data.format()} - ${LocalTime.now().format("HH:mm")}"
    val text2 = "Pedido : ${bean.numero}"
    val spaces = widthPage - text1.length - text2.length
    writeln(
      text = "$text1${" ".repeat(spaces)}$text2",
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
    column(ProdutoMovimentacao::codigoStr, "Codigo", 6)
    column(ProdutoMovimentacao::descricao, "Descricao", 36)
    column(ProdutoMovimentacao::grade, "Grade", 8)
    column(ProdutoMovimentacao::locApp, "Loc", 4)
    column(ProdutoMovimentacao::movimentacao, "_Quant", 6, lineBreak = true)
    column(ProdutoMovimentacao::barcodeRelatorio, "", size = 55)
  }

  override fun printSumary(bean: ProdutoMovimentacao?) {

    val entregueNome = bean?.entregueNome ?: ""
    val recebidoNome = bean?.recebidoNome ?: ""
    writeln("")
    writeln("")
    writeln("DOCUMENTO NÃO FISCAL", center = true)
    writeln("")
    writeln("")
  }
}