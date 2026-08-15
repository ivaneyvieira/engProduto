package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.DadosDev
import br.com.astrosoft.produto.model.beans.DadosDevProduto

class ValeTrocaDadosDev(val nota: DadosDev) : PrintText<DadosDevProduto>() {
  init {
    column(DadosDevProduto::codigoFormat, "Codigo", 6)
    column(DadosDevProduto::descricao, "Descricao", 41)
    column(DadosDevProduto::grade, "Grade", 8)
    column(DadosDevProduto::quantidadeTipo, "Qtd", 6)
  }

  val obsTipo = nota.obsTipo ?: ""

  private fun tituloValeTroca() {
    when {
      obsTipo.contains("TROCA")      -> {
        writeln("Vale Credito: TROCA", negrito = true, expand = true)
      }

      obsTipo.contains("ENTREGA")    -> {
        writeln("Vale Credito: ENTREGA", negrito = true, expand = true)
      }

      obsTipo.contains("RETIRA")     -> {
        writeln("Vale Credito: RETIRA", negrito = true, expand = true)
      }

      obsTipo.contains("REEMBOLSO")  -> {
        writeln("Vale Credito: REEMBOLSO", negrito = true, expand = true)
      }

      obsTipo.contains("GARANTIA")   -> {
        writeln("Vale Credito: GARANTIA", negrito = true, expand = true)
      }

      obsTipo.contains("MUDA NF")    -> {
        writeln("Vale Credito: MUDA NF", negrito = true, expand = true)
      }

      obsTipo.contains("MUDA")       -> {
        writeln("Vale Credito: MUDA CLIENTE", negrito = true, expand = true)
        writeln("Novo Cliente: ${nota.mudaCliente()}", negrito = true)
      }

      obsTipo.contains("EST CARTAO") -> {
        writeln("Vale Credito: ESTORNO CARTAO", negrito = true, expand = true)
      }

      obsTipo.contains("EST BOLETO") -> {
        writeln("Vale Credito: ESTORNO BOLETO", negrito = true, expand = true)
      }

      obsTipo.contains("EST DEP")    -> {
        writeln("Vale Credito: ESTORNO DE DEPOSITO", negrito = true, expand = true)
      }
    }
  }

  override fun groupBotton(beanDetail: DadosDevProduto): String {
    return if (beanDetail.produtoTroca == "M") {
      beanDetail.produtoTrocaItem ?: ""
    } else {
      ""
    }
  }

  override fun print(dados: List<DadosDevProduto>, printer: IPrinter) {
    super.print(dados.sortedBy { it.produtoTrocaItem }, printer)
  }

  data class Cliente(val custno: Int, val name: String)

  /*
  private fun DadosDev.clienteCredito(titulo: String): String {
    val reg = if (custnoVend in listOf(200, 300, 400, 500, 800)) {
      when {
        (custnoCli ?: 0) > 0  -> {
          Cliente(custnoCli ?: 0, nameCli ?: "")
        }

        (custnoMuda ?: 0) > 0 -> {
          Cliente(custnoMuda ?: 0, nameMuda ?: "")
        }

        (custnoObs ?: 0) > 0  -> {
          Cliente(custnoObs ?: 0, nameObs ?: "")
        }

        else                  -> {
          Cliente(codCliente ?: 0, nomeCliente ?: "")
        }
      }
    } else {
      Cliente(codCliente ?: 0, nomeCliente ?: "")
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
  }*/

  override fun printTitle(bean: DadosDevProduto) {
    writeln("Loja: ${nota.nomeLoja}", negrito = true, center = true, expand = true)
    tituloValeTroca()
    val totalTxt = "<E>Valor R$: ${nota.valorDev.format()}</E>"
    writeln(totalTxt, negrito = true)
    writeln("<E>NI: ${nota.ni} - </E>VALIDO ATE ${nota.dataDevolucao?.plusDays(0).format()}", negrito = true)
    //val clienteCredito = nota.clienteCredito("Credito: ")
    //if (clienteCredito.isNotBlank()) {
    //  writeln(clienteCredito, negrito = true)
    //}
    writeln("", negrito = true)
    writeln("Cliente Compra: <E>${nota.custnoVend}</E> - ${nota.nomeVend}", negrito = true)
    writeln(
      "NF Entrada: ${nota.nfVenda ?: ""} Data: ${nota.dataVenda.format()}",
      negrito = true
    )
    writeln("Cliente Devolucao : ${nota.codCliente} - ${nota.nomeCliente}", negrito = true)
    writeln("Referente: ${nota.obsNotaVenda}", negrito = true)
    writeln("Tipo Credito: ${nota.obsTipo}", negrito = true)
    writeln("Vendedor: ${nota.empno} - ${nota.vendedor}", negrito = true)
    printLine('-')
  }

  override fun printSumary(bean: DadosDevProduto?) {
    val autorizacao = if (nota.nomeTroca.isNullOrBlank()) {
      nota.nomeSolicitacao ?: ""
    } else {
      nota.nomeTroca ?: ""
    }

    val metadeWith = this.widthPage / 2

    val solicitacao = if (nota.nomeSolicitacao.isNullOrBlank()) {
      nota.nomeTroca ?: ""
    } else {
      nota.nomeSolicitacao ?: ""
    }.let { sol ->
      if (sol.length > metadeWith) {
        sol.substring(0, metadeWith)
      } else {
        sol
      }
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
    val len = (metadeWith - solicitacao.length) / 2
    val str = if (len > 0) {
      " ".repeat(len)
    } else {
      ""
    }

    writeln("_______________________________  _______________________________")
    writeln("${str}${solicitacao}${str}               Caixa")
    writeln("           Autorizacao")
    writeln("")
    writeln("")
  }
}