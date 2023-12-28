package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import br.com.astrosoft.produto.model.beans.EntradaDevCliPro

class ValeTrocaDevolucao(val nota: EntradaDevCli) : PrintText<EntradaDevCliPro>() {
  init {
    column(EntradaDevCliPro::codigoFormat, "Codigo", 6)
    column(EntradaDevCliPro::descricao, "Descricao", 41)
    column(EntradaDevCliPro::grade, "Grade", 8)
    column(EntradaDevCliPro::quantidade, "Qtd", 6)
  }

  private fun tituloValeTroca() {
    when {
      nota.observacao02.contains("TROCA")      -> {
        println("Vale Credito: TROCA", negrito = true, center = true)
      }

      nota.observacao02.contains("ENTREGA")    -> {
        println("Vale Credito: ENTREGA", negrito = true, center = true)
      }

      nota.observacao02.contains("RETIRA")     -> {
        println("Vale Credito: RETIRA", negrito = true, center = true)
      }

      nota.observacao02.contains("REEMBOLSO")  -> {
        println("Vale Credito: REEMBOLSO", negrito = true, center = true)
      }

      nota.observacao02.contains("MUDA NF")    -> {
        println("Vale Credito: MUDA NF", negrito = true, center = true)
      }

      nota.observacao02.contains("MUDA")       -> {
        println("Vale Credito: MUDA CLIENTE", negrito = true, center = true)
        println("Novo Cliente: ${nota.mudaCliente()}", negrito = true, center = true)
      }

      nota.observacao02.contains("EST CARTAO") -> {
        println("Vale Credito: ESTORNO CARTAO", negrito = true, center = true)
      }

      nota.observacao02.contains("EST BOLETO") -> {
        println("Vale Credito: ESTORNO BOLETO", negrito = true, center = true)
      }

      nota.observacao02.contains("EST DEP")    -> {
        println("Vale Credito: ESTORNO DE DEPOSITO", negrito = true, center = true)
      }
    }
  }

  override fun printTitle(bean: EntradaDevCliPro) {
    tituloValeTroca()
    println("VALIDO ATE ${nota.data?.plusDays(0).format()}", negrito = true, center = true)
    println("NI: ${nota.invno}", negrito = true, expand = true, center = true)
    println("", negrito = true)
    println("Loja: ${nota.nomeLoja}", negrito = true)
    println("Cliente Compra: ${nota.custno} - ${nota.cliente}", negrito = true)
    println(
      "NF Entrada: ${nota.notaFiscal ?: ""} Data: ${nota.data.format()} Hora: ${nota.hora}",
      negrito = true
    )
    println("Cliente Devolucao: ${nota.custnoDev} - ${nota.clienteDev}", negrito = true)
    println("Referente: ${nota.remarks ?: ""}", negrito = true)
    println("Vendedor: ${nota.empno} - ${nota.vendedor}", negrito = true)
    val totalTxt = "Valor Total do Vale Troca R$: ${nota.valor.format()}"
    println(totalTxt, negrito = true)
    printLine('-')
  }

  override fun printSumary() {
    println("")
    println("DOCUMENTO NAO FISCAL", center = true)
    println("")
    println("")
    println("______________________________________", center = true)
    println(nota.userName ?: "", center = true)
    println("Setor de Troca", center = true)
    println("")
    println("")
    println("_______________________________  _______________________________")
    println("            Gerencia                           Caixa")
    println("")
    println("")
  }
}