package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import br.com.astrosoft.produto.model.beans.EntradaDevCliPro

class ValeTrocaDevolucao(val nota: EntradaDevCli) : PrintText<EntradaDevCliPro>() {
  init {
    column(EntradaDevCliPro::codigoFormat, "Codigo", 6)
    column(EntradaDevCliPro::descricao, "Descricao", 41)
    column(EntradaDevCliPro::grade, "Grade", 8)
    column(EntradaDevCliPro::tipoQtdEfetiva, "Qtd", 6)
  }

  private fun tituloValeTroca() {
    when {
      nota.tipoObs.contains("TROCA")      -> {
        writeln("Vale Credito: TROCA", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("ENTREGA")    -> {
        writeln("Vale Credito: ENTREGA", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("RETIRA")     -> {
        writeln("Vale Credito: RETIRA", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("REEMBOLSO")  -> {
        writeln("Vale Credito: REEMBOLSO", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("GARANTIA")   -> {
        writeln("Vale Credito: GARANTIA", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("MUDA NF")    -> {
        writeln("Vale Credito: MUDA NF", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("MUDA")       -> {
        writeln("Vale Credito: MUDA CLIENTE", negrito = true, center = true, expand = true)
        writeln("Novo Cliente: ${nota.mudaCliente()}", negrito = true, center = true)
      }

      nota.tipoObs.contains("EST CARTAO") -> {
        writeln("Vale Credito: ESTORNO CARTAO", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("EST BOLETO") -> {
        writeln("Vale Credito: ESTORNO BOLETO", negrito = true, center = true, expand = true)
      }

      nota.tipoObs.contains("EST DEP")    -> {
        writeln("Vale Credito: ESTORNO DE DEPOSITO", negrito = true, center = true, expand = true)
      }
    }
  }

  override fun groupBotton(beanDetail: EntradaDevCliPro): String {
    return if (beanDetail.isTipoMisto()) {
      beanDetail.tipoPrdTratado()
    } else {
      ""
    }
  }

  override fun print(dados: List<EntradaDevCliPro>, printer: IPrinter) {
    super.print(dados.sortedBy { it.tipoPrd }, printer)
  }

  data class Cliente(val custno: Int, val name: String)

  private fun EntradaDevCli.clienteCredito(titulo: String): String {
    val reg = if (custnoVend in listOf(200, 300, 400, 500, 800)) {
      when {
        (custnoCli ?: 0) > 0  -> {
          Cliente(custnoCli ?: 0, nameCli ?: "")
        }

        (custnoMuda ?: 0) > 0 -> {
          Cliente(custnoMuda ?: 0, nameMuda ?: "")
        }

        else                  -> {
          Cliente(custnoDev ?: 0, clienteDev ?: "")
        }
      }
    } else {
      Cliente(custnoDev ?: 0, clienteDev ?: "")
    }

    if (reg.custno == 0) return ""

    val totalTitulo = titulo.length
    val totalSep = 3
    val totalCodigo = reg.custno.toString().length * 2
    val totalNome = reg.name.length
    val total = totalTitulo + totalSep + totalCodigo + totalNome
    val width = total - widthPage

    val regAjustado = if (width > 0) {
      reg.copy(name = reg.name.substring(0, reg.name.length - width))
    } else {
      reg
    }

    return "$titulo<E>${regAjustado.custno}</E> - ${regAjustado.name}"
  }

  override fun printTitle(bean: EntradaDevCliPro) {
    tituloValeTroca()
    writeln("Loja: ${nota.nomeLoja}", negrito = true, center = true)
    writeln("VALIDO ATE ${nota.data?.plusDays(0).format()}", negrito = true, center = true)
    writeln("NI: ${nota.invno}", negrito = true, expand = true, center = true)
    val clienteCredito = nota.clienteCredito("Credito: ")
    if (clienteCredito.isNotBlank()) {
      writeln(clienteCredito, negrito = true)
    }
    writeln("", negrito = true)
    writeln("Cliente Compra: <E>${nota.custnoVend}</E> - ${nota.cliente}", negrito = true)
    writeln(
      "NF Entrada: ${nota.notaFiscal ?: ""} Data: ${nota.data.format()} Hora: ${nota.hora}",
      negrito = true
    )
    writeln("Cliente Devolucao : ${nota.custnoDev} - ${nota.clienteDev}", negrito = true)
    writeln("Referente: ${nota.remarksLinha1}", negrito = true)
    writeln("Tipo Credito: ${nota.remarksLinha2}", negrito = true)
    writeln("Vendedor: ${nota.empno} - ${nota.vendedor}", negrito = true)
    val totalTxt = "Valor Total do Vale Troca <E>R$: ${nota.valor.format()}</E>"
    writeln(totalTxt, negrito = true)
    printLine('-')
  }

  override fun printSumary(bean: EntradaDevCliPro?) {
    val autorizacao = if (nota.nameAutorizacao.isNullOrBlank()) {
      nota.nameSolicitacao ?: ""
    } else {
      nota.nameAutorizacao ?: ""
    }

    val solicitacao = if (nota.nameSolicitacao.isNullOrBlank()) {
      nota.nameAutorizacao ?: ""
    } else {
      nota.nameSolicitacao ?: ""
    }

    writeln("")
    writeln("DOCUMENTO NAO FISCAL", center = true)
    writeln("")
    writeln("")
    writeln("______________________________________", center = true)
    writeln(autorizacao ?: "", center = true)
    writeln("Setor de Troca", center = true)
    writeln("")
    writeln("")
    val len = ("_______________________________  ".length - solicitacao.length) / 2
    val str = " ".repeat(len)

    writeln("_______________________________  _______________________________")
    writeln("${str}${solicitacao}${str}                     Caixa")
    writeln("           Autorizacao")
    writeln("")
    writeln("")
  }
}