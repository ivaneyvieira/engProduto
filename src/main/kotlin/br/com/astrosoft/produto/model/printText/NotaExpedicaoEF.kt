package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.NotaSaida
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import br.com.astrosoft.produto.model.beans.ProdutoPedido

class NotaExpedicaoEF(val nota: NotaSaida) : PrintText<ProdutoNFS>() {
  override fun printTitle(bean: ProdutoNFS) {
    writeln("Romaneio de Separacao para Retira: Reserva ${nota?.numero}", negrito = true, center = true)

    writeln("<B>Loja: </B>${nota?.loja}")
    writeln("<B>Usuario da Impressao: </B>${nota.usuarioEntrega ?: AppConfig.userLogin()?.name ?: ""}")
    writeln("<B>NF de Fatura: </B>${nota?.nota}/${nota?.nota}<B> Data: </B>${nota.data}<B> Hora: </B>${nota.hora}")
    writeln("<B>PDV: </B>${nota.pdvno}      <B> Valor: </B>${nota.valorNota.format()}")
    writeln("<B>Cliente: </B>${nota.cliente}")
    writeln("<B>Vendedor (a): </B>${nota.vendedor}")

    printLine()
  }

  init {
    column(ProdutoNFS::codigo, "Codigo", 6)
    column(ProdutoNFS::descricao, "Descricao", 40)
    column(ProdutoNFS::grade, "Grade", 9)
    column(ProdutoNFS::quantidade, "Quant", 6)
  }

  override fun printSumary(bean: ProdutoNFS?) {
    writeln("")
    writeln("DOCUMENTO NAO FISCAL", center = true)
    writeln("")
    writeln("")
  }
}