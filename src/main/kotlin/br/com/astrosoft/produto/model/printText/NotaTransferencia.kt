package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.produto.model.beans.ProdutoTransfRessu4

class NotaTransferencia : PrintText<ProdutoTransfRessu4>() {
  init {
    column(ProdutoTransfRessu4::codigoFormat, "Codigo", 6)
    column(ProdutoTransfRessu4::descricao, "Descricao", 40)
    column(ProdutoTransfRessu4::grade, "Grade", 9)
    column(ProdutoTransfRessu4::quant, "Quant", 6, lineBreak = true)
    column(ProdutoTransfRessu4::codigoBarras, "Codigo Barras", 21)
    column(ProdutoTransfRessu4::referencia, "Referencia", 42)
  }

  override fun printTitle(bean: ProdutoTransfRessu4) {
    val titulo = "NF Transf ${bean.notaTransf} - ${bean.rota}"
    val data = "Data: ${bean.data?.format() ?: "  /  /    "}".lpad(64 - titulo.length, " ")

    writeln("$titulo$data", negrito = true)
    writeln("".lpad(64, "-"), negrito = true)
  }

  override fun printSumary() {
    writeln("")
    writeln("")
    writeln("")
  }
}