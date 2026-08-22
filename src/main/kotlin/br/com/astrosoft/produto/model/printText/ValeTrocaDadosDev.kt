package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.mid
import br.com.astrosoft.framework.util.rpad
import br.com.astrosoft.produto.model.beans.DadosDev
import br.com.astrosoft.produto.model.beans.DadosDevProduto
import br.com.astrosoft.produto.model.beans.EProdutoTroca

class ValeTrocaDadosDev(val nota: DadosDev) : PrintText<DadosDevProduto>() {
  init {
    column(DadosDevProduto::codigoFormat, "", 6)
    column(DadosDevProduto::descricao, "", 41)
    column(DadosDevProduto::grade, "", 8)
    column(DadosDevProduto::quantidadeTotal, "", 6)
  }

  val obsTipo = nota.obsTipo ?: ""

  private fun tituloValeTroca() {
    when {
      obsTipo.contains("TROCA")      -> {
        writeln("Credito: $obsTipo", negrito = true, expand = true)
      }

      obsTipo.contains("ENTREGA")    -> {
        writeln("Credito: $obsTipo", negrito = true, expand = true)
      }

      obsTipo.contains("RETIRA")     -> {
        writeln("Credito: $obsTipo", negrito = true, expand = true)
      }

      obsTipo.contains("REEMBOLSO")  -> {
        writeln("Credito: $obsTipo", negrito = true, expand = true)
      }

      obsTipo.contains("GARANTIA")   -> {
        writeln("Credito: $obsTipo", negrito = true, expand = true)
      }

      obsTipo.contains("MUDA NF")    -> {
        writeln("Credito: $obsTipo", negrito = true, expand = true)
      }

      obsTipo.contains("MUDA")       -> {
        writeln("Credito: $obsTipo", negrito = true, expand = true)
        writeln("Novo Cliente: ${nota.mudaCliente()}", negrito = true)
      }

      obsTipo.contains("EST CARTAO") -> {
        writeln("Credito: ESTORNO CARTAO", negrito = true, expand = true)
      }

      obsTipo.contains("EST BOLETO") -> {
        writeln("Credito: ESTORNO BOLETO", negrito = true, expand = true)
      }

      obsTipo.contains("EST DEP")    -> {
        writeln("Credito: ESTORNO DE DEPOSITO", negrito = true, expand = true)
      }
    }
  }

  override fun groupBotton(beanDetail: DadosDevProduto): String {
    return beanDetail.produtoTrocaItemEnum?.descricao ?: ""
  }

  private fun List<DadosDevProduto>.explode(): List<DadosDevProduto> {
    return this.flatMap { prd ->
      val tipo = prd.produtoTrocaItemEnum ?: return@flatMap emptyList()
      if (tipo == EProdutoTroca.Misto) {
        val prdCom = prd.copy(quantidadeSem = 0)
        val prdSem = prd.copy(quantidadeCom = 0)
        listOf(prdCom, prdSem)
      } else {
        listOf(prd)
      }
    }
  }

  override fun print(dados: List<DadosDevProduto>, printer: IPrinter) {
    super.print(dados.explode().sortedBy { it.produtoTrocaItemEnum?.codigo ?: "" }, printer)
  }

  data class Cliente(val custno: Int, val name: String)

  private fun DadosDev.clienteCredito(titulo: String): String {
    val reg = if (custnoVend in listOf(200, 300, 400, 500, 800)) {
      when {
        (custnoObs ?: 0) > 0 -> {
          Cliente(custnoObs ?: 0, nomeClienteObs ?: "")
        }

        else                 -> {
          Cliente(custnoVend ?: 0, nomeVend ?: "")
        }
      }
    } else {
      Cliente(custnoVend ?: 0, nomeVend ?: "")
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

  override fun printTitle(bean: DadosDevProduto) {
    writeln("ENGECOPI ${nota.nomeLoja}", negrito = true, center = true, expand = true)
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
      "NF Entrada: ${nota.nfDevolucao} Data: ${nota.dataDevolucao.format()}",
      negrito = true
    )
    val nameWidth = widthPage - 19 - 15
    val nomeCliente = nota.nomeCliente?.rpad(100, " ") ?: ""
    writeln("Cliente Devolucao : ${nota.codCliente} - ${nomeCliente.mid(0, nameWidth)}", negrito = true)
    writeln("NF Venda: ${nota.nfVenda ?: ""} Data: ${nota.dataVenda.format()}", negrito = true)
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
    writeln(autorizacao, center = true)
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