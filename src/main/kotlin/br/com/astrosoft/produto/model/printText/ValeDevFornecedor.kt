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
    val titulo = "Pedido: ${nota.numeroDevolucao ?: 0}  Motivo: ${nota.motivoDevolucaoName}"
    
    if (titulo.length < (widthPage / 2)) {
      writeln(titulo, expand = true, negrito = true)
    } else {
      writeln("Pedido: ${nota.numeroDevolucao ?: 0}", expand = true, negrito = true)
      writeln("Motivo: ${nota.motivoDevolucaoName}", expand = true, negrito = true)
    }
    
    writeln("")
    writeln("")
    
    writeln("NI: ${nota.niPrincipal ?: 0} NFO: ${nota.nfEntrada ?: ""}", expand = true, negrito = true)
    writeln(
      "TRANSP: ${(nota.transpDevolucao ?: 0)} VOL: ${(nota.volumeDevolucao ?: 0)} PESO: ${
        (nota.pesoDevolucao ?: 0.00).format()
      }", expand = true, negrito = true
    )
    
    writeln("")
    writeln("")
  }
  
  override fun printSumary(bean: NotaRecebimentoProdutoDev?) {
    writeln("")
    writeln("")
    writeln("Total Produtos: ${nota.valorTotalProduto.format().lpad(11, " ")}".lpad(64, " "), negrito = true)
  }
}