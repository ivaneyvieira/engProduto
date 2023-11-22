package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import br.com.astrosoft.produto.model.beans.EntradaDevCliPro
import java.time.LocalDate

class ValeTrocaDevolucao(val nota: EntradaDevCli) : PrintText<EntradaDevCliPro>() {
  init {
    column(EntradaDevCliPro::codigo, "Codigo", 6)
    column(EntradaDevCliPro::descricao, "Descricao", 25)
    column(EntradaDevCliPro::grade, "Grade", 8)
    column(EntradaDevCliPro::quantidade, "Qtd", 6)
    column(EntradaDevCliPro::valorUnitario, "V. Unit", 6)
    column(EntradaDevCliPro::valorTotal, "V. Total", 8)
  }

  override fun printTitle(bean: EntradaDevCliPro) {
    println("Vale Troca Valido ate ${LocalDate.now().plusDays(2).format()}", negrito = true)
    println("", negrito = true)
    println("Loja: ${nota.nomeLoja}", negrito = true)
    println("Cliente: ${nota.custno} - ${nota.cliente}", negrito = true)
    println("NI: ${nota.invno}  NF Entrada: ${nota.notaFiscal ?: ""} Data: ${nota.data.format()}", negrito = true)
    println("Referente: ${nota.remarks ?: ""}", negrito = true)
    println("".padEnd(64, '-'), negrito = true)
  }

  override fun printSumary() {
    println("")
    println("      DOCUMENTO NAO FISCAL".expandido())
    println("")
    println("")
    println("____________________  ____________________  ____________________")
    println("      Gerencia              S. Troca               Caixa")
  }
}