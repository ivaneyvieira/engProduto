package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.NotaSaidaDev
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import br.com.astrosoft.produto.model.beans.UserSaci

class NotaExpedicaoDev(val nota: NotaSaidaDev) : PrintText<ProdutoNFS>() {
  init {
    column(ProdutoNFS::codigo, "Codigo", 6)
    column(ProdutoNFS::descricao, "Descricao", 36)
    column(ProdutoNFS::grade, "Grade", 8)
    column(ProdutoNFS::local, "Loc", 4)
    column(ProdutoNFS::quantidade, "_Quant", 6)
  }

  override fun printTitle(bean: ProdutoNFS) {
    writeln("Requisicao de Autorizacao de Retira em Outra Loja", negrito = true)
    val user = AppConfig.userLogin() as? UserSaci
    writeln("Usuario: ${user?.name}", negrito = true)

    writeln("".lpad(64, "-"), negrito = true)
  }

  override fun printSumary(bean: ProdutoNFS?) {
    writeln("")
    writeln("")
    writeln("")
  }
}