package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.EntradaDevCliProList

class ProdutosDevolucao() : PrintText<EntradaDevCliProList>() {
  init {
    column(EntradaDevCliProList::codigoFormat, "Codigo", 6)
    column(EntradaDevCliProList::descricao, "Descricao", 41)
    column(EntradaDevCliProList::grade, "Grade", 8)
    column(EntradaDevCliProList::quantidade, "Qtd", 6)
  }

  override fun printTitle(bean: EntradaDevCliProList) {
    println("Loja: ${bean.loja}", negrito = true)
    println("Produtos de Devolucoes de Clientes", negrito = true)
    println("Data: ${bean.data.format()}", negrito = true)

    printLine('-')
  }

  override fun printSumary() {
    println("")
    println("DOCUMENTO NAO FISCAL", center = true)
    println("")
    println("")
    println("_________________________________", center = true)
    println("Conferido", center = true)
    println("")
    println("")
  }
}