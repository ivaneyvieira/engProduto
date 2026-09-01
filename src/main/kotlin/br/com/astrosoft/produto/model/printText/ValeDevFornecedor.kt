package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.framework.util.rpad
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev

class ValeDevFornecedor(val nota: NotaRecebimentoDev) : PrintText<NotaRecebimentoProdutoDev>() {
  init {
    column(NotaRecebimentoProdutoDev::quantDevolucao, "Quant", 5)
    column(NotaRecebimentoProdutoDev::codigoFormat, "Codigo", 6)
    column(NotaRecebimentoProdutoDev::descricao, "Descricao", 31)
    column(NotaRecebimentoProdutoDev::grade, "Grade", 8)
    column(NotaRecebimentoProdutoDev::valorUnit, "Valor Unit", 10)
  }

  data class DadosNota(val titulo: String, val valor: String) {
    val length
      get() = if (titulo.length > valor.length) titulo.length else valor.length

    fun tituloFormatado(): String {
      return formataCentralizado(titulo)
    }

    fun valorFormatado(): String {
      return formataCentralizado(valor)
    }

    private fun formataCentralizado(valor: String): String {
      val len = valor.length
      val espaco = (length - len) / 2
      val valorCenter = valor.rpad(espaco + len, " ").lpad(length, " ")
      return valorCenter
    }
  }

  override fun printTitle(bean: NotaRecebimentoProdutoDev) {
    val listaTitulo = listOf(
      DadosNota(titulo = "Pedido", valor = (nota.numeroDevolucao ?: 0).toString()),
      DadosNota(titulo = "Motivo", valor = nota.motivoDevolucaoName)
    )

    val lenDadosTitulo = listaTitulo.sumOf { it.length }
    val espacoTitulo = (widthPage - lenDadosTitulo) / (listaTitulo.size + 1)

    val linhaColunaTitulo = listaTitulo.joinToString("") {
      " ".repeat(espacoTitulo) + it.tituloFormatado()
    }

    val linhaValorTitulo = listaTitulo.joinToString("") {
      " ".repeat(espacoTitulo) + it.valorFormatado()
    }

    writeln(linhaColunaTitulo, negrito = true)
    writeln(linhaValorTitulo)
    writeln("")

    val listaDados = listOf(
      DadosNota(titulo = "NI", valor = (nota.niPrincipal ?: 0).toString()),
      DadosNota(titulo = "NFO", valor = (nota.notaDevolucao ?: "")),
      DadosNota(titulo = "Transp", valor = (nota.transp ?: 0).toString()),
      DadosNota(titulo = "Volumes", valor = (nota.volume ?: 0).toString()),
      DadosNota(titulo = "Peso", valor = (nota.pesoDevolucao ?: 0.00).format()),
    )

    val lenDados = listaDados.sumOf { it.length }
    val espaco = (widthPage - lenDados) / (listaDados.size + 1)

    val linhaColuna = listaDados.joinToString("") {
      " ".repeat(espaco) + it.tituloFormatado()
    }

    val linhaValor = listaDados.joinToString("") {
      " ".repeat(espaco) + it.valorFormatado()
    }

    writeln(linhaColuna, negrito = true)
    writeln(linhaValor, negrito = false)
    writeln("")
    writeln("")
  }

  override fun printSumary(bean: NotaRecebimentoProdutoDev?) {
    writeln("")
    writeln("")
    writeln("Total Produtos: ${nota.valorTotalProduto.format().lpad(11, " ")}".lpad(64, " "), negrito = true)
  }
}