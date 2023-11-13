package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.viewmodel.PrintText
import br.com.astrosoft.produto.model.beans.ProdutoTransfRessu4

class NotaTransferencia : PrintText<ProdutoTransfRessu4>() {
  init {
    column(ProdutoTransfRessu4::codigoFormat, "Codigo", 6)
    column(ProdutoTransfRessu4::descricao, "Descricao", 37)
    column(ProdutoTransfRessu4::grade, "Grade", 9, lineBreak = true)
    column(ProdutoTransfRessu4::codigoBarras, "Codigo Barras", 15)
    column(ProdutoTransfRessu4::referencia, "Referencia", 31)
    column(ProdutoTransfRessu4::quant, "Quant", 6)
  }

  override fun titleLines(bean: ProdutoTransfRessu4): List<String> {
    return listOf(
      "NF Transf ${bean.notaTransf} - ${bean.rota}",
      "".lpad(54, "-")
    )
  }
}