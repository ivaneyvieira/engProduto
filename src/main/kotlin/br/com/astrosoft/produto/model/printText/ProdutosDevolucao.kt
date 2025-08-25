package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.EntradaDevCliProList

class ProdutosDevolucao(val titulo: String) : PrintText<EntradaDevCliProList>() {
  init {
    column(EntradaDevCliProList::codigoFormat, "Codigo", 6)
    column(EntradaDevCliProList::descricao, "Descricao", 41)
    column(EntradaDevCliProList::grade, "Grade", 8)
    column(EntradaDevCliProList::tipoQtdEfetiva, "Qtd", 6)
  }

  override fun groupBotton(beanDetail: EntradaDevCliProList): String {
    val finalTroca = if (beanDetail.isTipoMisto()) {
      beanDetail.tipoPrdTratado()
    } else {
      beanDetail.tipo ?: ""
    }
    return "$finalTroca - NI ${beanDetail.ni} NF ${beanDetail.nota} DATA ${beanDetail.data.format()} - ${beanDetail.userLogin}"
  }

  override fun printTitle(bean: EntradaDevCliProList) {
    writeln("Loja: ${bean.loja}", negrito = true)
    writeln(titulo, negrito = true)
    writeln("Data: ${bean.data.format()}", negrito = true)
    writeln("Usuario da Impressao: ${AppConfig.userLogin()?.name}", negrito = true)

    printLine('-')
  }

  override fun printSumary(bean: EntradaDevCliProList?) {
    writeln("")
    writeln("DOCUMENTO NAO FISCAL", center = true)
    writeln("")
    writeln("")
    writeln("_________________________________", center = true)
    writeln("Conferido", center = true)
    writeln("")
    writeln("")
  }
}