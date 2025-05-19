package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.produto.model.beans.NotaSaidaDev
import br.com.astrosoft.produto.model.beans.NotaSaidaDevProduto
import br.com.astrosoft.produto.model.beans.UserSaci

class NotaExpedicaoDev(val nota: NotaSaidaDev) : PrintText<NotaSaidaDevProduto>() {
  init {
    column(NotaSaidaDevProduto::codigo, "Codigo", 6)
    column(NotaSaidaDevProduto::descricao, "Descricao", 36)
    column(NotaSaidaDevProduto::grade, "Grade", 8)
    column(NotaSaidaDevProduto::local, "Loc", 4)
    column(NotaSaidaDevProduto::quantidade, "_Quant", 6)
  }

  override fun printTitle(bean: NotaSaidaDevProduto) {
    writeln("Requisicao de Autorizacao de Retira em Outra Loja", negrito = true)
    val user = AppConfig.userLogin() as? UserSaci
    writeln("Usuario: ${user?.name}", negrito = true)

    writeln("".lpad(64, "-"), negrito = true)
  }

  override fun printSumary(bean: NotaSaidaDevProduto?) {
    writeln("")
    writeln("")
    writeln("")
  }
}