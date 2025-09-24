package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.produto.model.beans.NotaRecebimento
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProduto

class PrintNotaDoc : PrintText<NotaRecebimento>() {
  override fun printTitle(bean: NotaRecebimento) {
    writeln("Relatório de Notas Fiscais de Entrada Recebidas", negrito = true, center = true)
    writeln("Loja : ${bean.loja} - ${bean.lojaSigla}", negrito = true, center = true)
    printLine()
  }

  init {
    column(NotaRecebimento::dataStr, "Data", 8)
    column(NotaRecebimento::emissaoStr, "Emissao", 8)
    column(NotaRecebimento::niStr, "NI", 7)
    column(NotaRecebimento::nfEntradaStr, "NF Ent", 12)
    column(NotaRecebimento::fornecedor, "Nome Fornecedor", 25)
  }

  override fun printSumary(bean: NotaRecebimento?) {
    writeln("")
    writeln("")
    writeln("")
    writeln("")
  }
}