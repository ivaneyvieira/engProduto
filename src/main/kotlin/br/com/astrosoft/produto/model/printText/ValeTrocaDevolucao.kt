package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import br.com.astrosoft.produto.model.beans.EntradaDevCliPro

class ValeTrocaDevolucao(val nota: EntradaDevCli, val autorizacao: String = "") : PrintText<EntradaDevCliPro>() {
  init {
    column(EntradaDevCliPro::codigoFormat, "Codigo", 6)
    column(EntradaDevCliPro::descricao, "Descricao", 41)
    column(EntradaDevCliPro::grade, "Grade", 8)
    column(EntradaDevCliPro::quantidade, "Qtd", 6)
  }

  private fun tituloValeTroca() {
    when {
      nota.tipoObs.contains("TROCA") -> {
        writeln("Vale Credito: TROCA", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("ENTREGA") -> {
        writeln("Vale Credito: ENTREGA", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("RETIRA") -> {
        writeln("Vale Credito: RETIRA", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("REEMBOLSO") -> {
        writeln("Vale Credito: REEMBOLSO", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("GARANTIA") -> {
        writeln("Vale Credito: GARANTIA", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("MUDA NF") -> {
        writeln("Vale Credito: MUDA NF", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("MUDA") -> {
        writeln("Vale Credito: MUDA CLIENTE", negrito = true, center = true, expand = true)
        writeln("Novo Cliente: ${nota.mudaCliente()}", negrito = true, center = true)
      }

      nota.tipoObs.contains("EST CARTAO") -> {
        writeln("Vale Credito: ESTORNO CARTAO", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("EST BOLETO") -> {
        writeln("Vale Credito: ESTORNO BOLETO", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("EST DEP") -> {
        writeln("Vale Credito: ESTORNO DE DEPOSITO", negrito = true, center = true, expand = true)
      }
    }
  }

  override fun printTitle(bean: EntradaDevCliPro) {
    tituloValeTroca()
    writeln("VALIDO ATE ${nota.data?.plusDays(0).format()}", negrito = true, center = true)
    writeln("NI: ${nota.invno}", negrito = true, expand = true, center = true)
    writeln("", negrito = true)
    writeln("Loja: ${nota.nomeLoja}", negrito = true)
    writeln("Cliente Compra: <E>${nota.custno}</E> - ${nota.cliente}", negrito = true)
    writeln(
      "NF Entrada: ${nota.notaFiscal ?: ""} Data: ${nota.data.format()} Hora: ${nota.hora}",
      negrito = true
    )
    writeln("Cliente Devolucao: ${nota.custnoDev} - ${nota.clienteDev}", negrito = true)
    writeln("Referente: ${nota.remarks ?: ""}", negrito = true)
    writeln("Vendedor: ${nota.empno} - ${nota.vendedor}", negrito = true)
    val totalTxt = "Valor Total do Vale Troca <E>R$: ${nota.valor.format()}</E>"
    writeln(totalTxt, negrito = true)
    printLine('-')
  }

  override fun printSumary(bean: EntradaDevCliPro?) {
    writeln("")
    writeln("DOCUMENTO NAO FISCAL", center = true)
    writeln("")
    writeln("")
    writeln("______________________________________", center = true)
    writeln(nota.userName ?: "", center = true)
    writeln("Setor de Troca", center = true)
    writeln("")
    writeln("")
    if (autorizacao.isBlank()) {
      writeln("_______________________________  _______________________________")
      writeln("            Gerencia                           Caixa")
    } else {
      val len = ("_______________________________  ".length - autorizacao.length) / 2
      val str = " ".repeat(len)

      writeln("_______________________________  _______________________________")
      writeln("${str}${autorizacao}${str}                     Caixa")
      writeln("           Autorizacao")
    }
    writeln("")
    writeln("")
  }
}