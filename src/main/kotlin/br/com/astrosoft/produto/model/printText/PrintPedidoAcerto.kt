package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.PedidoAcerto
import br.com.astrosoft.produto.model.beans.ProdutoAcerto
import kotlin.reflect.KProperty1

class PrintPedidoAcerto(val pedido: PedidoAcerto, propertyQuant: KProperty1<ProdutoAcerto, Int?>) :
  PrintText<ProdutoAcerto>() {
  override fun printTitle(bean: ProdutoAcerto) {
    val titulo = pedido.observacao?.trim()?.substringAfter(" ")?.trim() ?: ""
    writeln(titulo, negrito = true, center = true)
    writeln("")
    writeln(
      "Data: ${pedido.data.format()}   Pedido: ${pedido.loja}.${pedido.pedido}",
      negrito = true
    )
    val user = AppConfig.userLogin()
    writeln("Usuario: ${user?.login}", negrito = true)

    printLine()
  }

  init {
    if (pedido.loja == 1 && (pedido.pedido ?: 0) >= 2 && (pedido.pedido ?: 0) <= 8) {
      column(ProdutoAcerto::codigo, "Codigo", 6)
      column(ProdutoAcerto::descricao, "Descricao", 40)
      column(ProdutoAcerto::grade, "Grade", 8)
      column(ProdutoAcerto::estoque, "Estoque", 7)
    } else if (pedido.loja == 1 && (pedido.pedido ?: 0) >= 22 && (pedido.pedido ?: 0) <= 88) {
      column(ProdutoAcerto::codigo, "Codigo", 6)
      column(ProdutoAcerto::descricao, "Descricao", 40)
      column(ProdutoAcerto::grade, "Grade", 8)
      column(ProdutoAcerto::estoque, "Estoque", 7)
    } else {
      column(ProdutoAcerto::codigo, "Codigo", 6)
      column(ProdutoAcerto::descricao, "Descricao", 34)
      column(ProdutoAcerto::grade, "Grade", 8)
      column(propertyQuant, "Quant", 5)
      column(ProdutoAcerto::estoque, "Estoque", 7)
    }
  }

  override fun printSumary(bean: ProdutoAcerto?) {
    writeln("")
    writeln("")
  }
}