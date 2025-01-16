package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProduto
import java.time.LocalDate

class PrintNotaRecebimento() : PrintText<NotaRecebimentoProduto>() {
  private var valorPedido: Double = 0.0
  override fun printTitle(bean: NotaRecebimentoProduto) {
    writeln("Nota Recebimento", negrito = true, center = true)
    writeln("")
    writeln(
      "Data: ${LocalDate.now().format()}     NI: ${bean.ni}  NF: ${bean.nfEntrada}",
      negrito = true
    )
    writeln(
      text = "Loja: ${bean.lojaSigla}   Fornecedor: ${bean.fornecedor}",
      negrito = true
    )
    writeln(
      text = "Recebido: ${bean.usuarioRecebe}",
      negrito = true
    )
    writeln(
      text = "Usuario: ${AppConfig.userLogin()?.name}",
      negrito = true
    )

    printLine()
  }

  init {
    column(NotaRecebimentoProduto::codigo, "Codigo", 6)
    column(NotaRecebimentoProduto::descricao, "Descricao", 40)
    column(NotaRecebimentoProduto::grade, "Grade", 8)
    column(NotaRecebimentoProduto::quant, "__Quant", 7, lineBreak = true)
    column(NotaRecebimentoProduto::localizacaoSaciStr, "", 35)
  }

  override fun printSumary(bean: NotaRecebimentoProduto?) {
    writeln("")
    writeln("")
    writeln("")
    writeln("")
  }
}