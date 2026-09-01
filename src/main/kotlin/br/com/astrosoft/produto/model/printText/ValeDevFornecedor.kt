package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev

class ValeDevFornecedor(val nota: NotaRecebimentoDev) : PrintText<NotaRecebimentoProdutoDev>() {
  init {
    column(NotaRecebimentoProdutoDev::quantDevolucao, "Quant", 5)
    column(NotaRecebimentoProdutoDev::codigoFormat, "Codigo", 6)
    column(NotaRecebimentoProdutoDev::descricao, "Descricao", 31)
    column(NotaRecebimentoProdutoDev::grade, "Grade", 8)
    column(NotaRecebimentoProdutoDev::valorUnit, "Valor Unit", 10)
  }

  override fun printTitle(bean: NotaRecebimentoProdutoDev) {
    writeln("Pedido Motivo", negrito = true, center = true)
    val pedido = (nota.numeroDevolucao ?: 0).toString().lpad(6, " ").lpad(31, " ")
    val motivo = nota.motivoDevolucaoName
    writeln("$pedido $motivo")
    writeln("")

    val listColunas = listOf(
      "NI".lpad(10, " "),
      "NFO".lpad(10, " "),
      "Entrada".lpad(10, " "),
      "Transp".lpad(6, " "),
      "Volumes".lpad(7, " "),
      "Peso".lpad(10, " ")
    )
    val listValores = listOf(
      (nota.niPrincipal ?: 0).toString().lpad(10, " "),
      (nota.notaDevolucao ?: "").lpad(10, " "),
      nota.dataEntrada.format().lpad(10, " "),
      (nota.transp ?: 0).toString().lpad(6, " "),
      (nota.volume ?: 0).toString().lpad(7, " "),
      (nota.pesoDevolucao ?: 0.00).format().lpad(10, " "),
    )
    writeln(listColunas.joinToString(" "), negrito = true)
    writeln(listValores.joinToString(" "), negrito = false)
    writeln("")
    writeln("")
  }

  override fun printSumary(bean: NotaRecebimentoProdutoDev?) {
    writeln("")
    writeln("")
    writeln("Total Produtos: ${nota.valorTotalProduto.format().lpad(11, " ")}".lpad(64, " "), negrito = true)
  }
}