package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.produto.model.beans.ProdutoEstoqueAcerto

class PrintProdutosConferenciaAcerto : PrintText<ProdutoEstoqueAcerto>() {
  override fun printTitle(bean: ProdutoEstoqueAcerto) {
    writeln("Pedido de acerto: ${bean.numero}", negrito = true, center = true)
    writeln("")
    writeln(
      "Loja: ${bean.lojaSigla}     Data: ${bean.data.format()}     Hora: ${bean.hora.format()}",
      negrito = true
    )
    writeln(
      text = "Usuario: ${bean.usuario}",
      negrito = true
    )

    printLine()
  }

  override fun groupBotton(beanDetail: ProdutoEstoqueAcerto): String {
    val dif = beanDetail.diferenca
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

  init {
    column(ProdutoEstoqueAcerto::codigo, "Codigo", 6)
    column(ProdutoEstoqueAcerto::descricao, "Descricao", 33)
    column(ProdutoEstoqueAcerto::grade, "Grade", 8)
    column(ProdutoEstoqueAcerto::diferenca, "_____Diferenca", 14, lineBreak = true)
    column(ProdutoEstoqueAcerto::estoqueRelatorio, size = 60)
  }

  override fun printSumary(bean: ProdutoEstoqueAcerto?) {
    writeln("")
    writeln("")
    writeln("")
    writeln("")
  }
}