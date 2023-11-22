package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import br.com.astrosoft.produto.model.beans.EntradaDevCliPro
import java.time.LocalDate

class ValeTrocaDevolucao(val nota: EntradaDevCli) : PrintText<EntradaDevCliPro>() {
  init {
    column(EntradaDevCliPro::codigo, "Codigo", 6)
    column(EntradaDevCliPro::descricao, "Descricao", 41)
    column(EntradaDevCliPro::grade, "Grade", 8)
    column(EntradaDevCliPro::quantidade, "Qtd", 6)
  }

  override fun printTitle(bean: EntradaDevCliPro) {
    println("VALE TROCA", negrito = true, center = true)
    println("VALIDO ATE ${LocalDate.now().plusDays(2).format()}", negrito = true, center = true)
    println("", negrito = true)
    println("Loja: ${nota.nomeLoja}", negrito = true)
    println("Cliente: ${nota.custno} - ${nota.cliente}", negrito = true)
    println("NI: ${nota.invno}  NF Entrada: ${nota.notaFiscal ?: ""} Data: ${nota.data.format()}", negrito = true)
    println("Referente: ${nota.remarks ?: ""}", negrito = true)
    printLine('-')
  }

  override fun printSumary() {
    val totalTxt = "Valor Total R$: ${nota.produtos().sumOf { it.valorTotal ?: 0.00}.format()}"
    println(" ".repeat(64 - totalTxt.length) + totalTxt, negrito = true)
    println("")
    println("DOCUMENTO NAO FISCAL".expandido(), center = true)
    println("")
    println("")
    println("____________________  ____________________  ____________________")
    println("      Gerencia              S. Troca               Caixa")
    println("")
    println("")
  }
}