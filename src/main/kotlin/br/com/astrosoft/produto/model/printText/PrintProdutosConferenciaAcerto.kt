package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.produto.model.beans.FiltroProdutoEstoque
import br.com.astrosoft.produto.model.beans.ProdutoEstoque
import java.time.LocalDate
import java.time.LocalTime

class PrintProdutosConferenciaAcerto(private val filtro: FiltroProdutoEstoque) : PrintText<ProdutoEstoque>() {
  private var valorPedido: Double = 0.0
  override fun printTitle(bean: ProdutoEstoque) {
    writeln("Relatório de Acerto", negrito = true, center = true)
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

  override fun groupBotton(beanDetail: ProdutoEstoque): String {
    val dif = beanDetail.estoqueDif
    val linha = "".lpad(64, "-")
    return when {
      dif == null -> {
        "Nao preenchido\n$linha"
      }

      dif > 0     -> {
        "Entrada\n$linha"
      }

      dif == 0    -> {
        "Zero\n$linha"
      }

      dif < 0     -> {
        "Saida\n$linha"
      }

      else        -> linha
    }
  }

  override fun print(dados: List<ProdutoEstoque>, printer: IPrinter) {
    valorPedido = dados.sumOf { ((it.estoque ?: 0) * 1.00) }
    super.print(dados, printer)
  }

  init {
    column(ProdutoEstoque::codigoStr, "Codigo", 6)
    column(ProdutoEstoque::descricao, "Descricao", 33)
    column(ProdutoEstoque::grade, "Grade", 8)
    column(ProdutoEstoque::estoqueDif, "_____Diferenca", 7, expand = true)
  }

  override fun printSumary(bean: ProdutoEstoque?) {
    writeln("")
    writeln("")
    writeln("")
    writeln("")
  }
}