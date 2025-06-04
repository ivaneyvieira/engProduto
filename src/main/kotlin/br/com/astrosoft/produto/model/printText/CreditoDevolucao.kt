package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.CreditoCliente
import br.com.astrosoft.produto.model.beans.EntradaDevCliPro

class CreditoDevolucao(val nota: CreditoCliente, val autorizacao: String = "") : PrintText<EntradaDevCliPro>() {
  init {
    column(EntradaDevCliPro::codigoFormat, "Codigo", 6)
    column(EntradaDevCliPro::descricao, "Descricao", 41)
    column(EntradaDevCliPro::grade, "Grade", 8)
    column(EntradaDevCliPro::quantidade, "Qtd", 6)
  }

  private fun tituloValeTroca() {
    val tipo = nota.tipo ?: ""
    when {
      tipo.contains("TROCA")      -> {
        writeln("Vale Credito: TROCA", negrito = true, center = true, expand = true)
      }

      tipo.contains("ENTREGA")    -> {
        writeln("Vale Credito: ENTREGA", negrito = true, center = true, expand = true)
      }

      tipo.contains("RETIRA")     -> {
        writeln("Vale Credito: RETIRA", negrito = true, center = true, expand = true)
      }

      tipo.contains("REEMBOLSO")  -> {
        writeln("Vale Credito: REEMBOLSO", negrito = true, center = true, expand = true)
      }

      tipo.contains("GARANTIA")   -> {
        writeln("Vale Credito: GARANTIA", negrito = true, center = true, expand = true)
      }

      tipo.contains("MUDA NF")    -> {
        writeln("Vale Credito: MUDA NF", negrito = true, center = true, expand = true)
      }

      tipo.contains("MUDA")       -> {
        writeln("Vale Credito: MUDA CLIENTE", negrito = true, center = true, expand = true)
        writeln("Novo Cliente: ${nota.mudaCliente()}", negrito = true, center = true)
      }

      tipo.contains("EST CARTAO") -> {
        writeln("Vale Credito: ESTORNO CARTAO", negrito = true, center = true, expand = true)
      }

      tipo.contains("EST BOLETO") -> {
        writeln("Vale Credito: ESTORNO BOLETO", negrito = true, center = true, expand = true)
      }

      tipo.contains("EST DEP")    -> {
        writeln("Vale Credito: ESTORNO DE DEPOSITO", negrito = true, center = true, expand = true)
      }
    }
  }

  override fun printTitle(bean: EntradaDevCliPro) {
    tituloValeTroca()
    writeln("VALIDO ATE ${nota.dtDev?.plusDays(0).format()}", negrito = true, center = true)
    writeln("NI: ${nota.invno}", negrito = true, expand = true, center = true)
    writeln("", negrito = true)
    writeln("Loja: ${nota.loja}", negrito = true)
    writeln("Cliente Compra: <E>${nota.codigo}</E> - ${nota.nome}", negrito = true)
    writeln(
      "NF Entrada: ${nota.nfDev ?: ""} Data: ${nota.dtDev.format()} Hora: ",
      negrito = true
    )
    writeln("Cliente Devolucao: ${nota.codigo} - ${nota.nfVenda}", negrito = true)
    writeln("Vendedor:  - ", negrito = true)
    val totalTxt = "Valor Total do Vale Troca <E>R$: ${nota.vlCredito.format()}</E>"
    writeln(totalTxt, negrito = true)
    printLine('-')
  }

  override fun printSumary(bean: EntradaDevCliPro?) {
    writeln("")
    writeln("DOCUMENTO NAO FISCAL", center = true)
    writeln("")
    writeln("")
    writeln("______________________________________", center = true)
    val userName = AppConfig.userLogin()?.name
    writeln(userName ?: "", center = true)
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