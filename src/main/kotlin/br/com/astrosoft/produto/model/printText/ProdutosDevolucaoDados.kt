package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.DadosDevProduto
import br.com.astrosoft.produto.model.beans.EProdutoTroca

class ProdutosDevolucaoDados(val titulo: String) : PrintText<DadosDevProduto>() {
  init {
    column(DadosDevProduto::codigoFormat, "Codigo", 6)
    column(DadosDevProduto::descricao, "Descricao", 41)
    column(DadosDevProduto::grade, "Grade", 8)
    column(DadosDevProduto::quantidadeDev, "Qtd", 6)
  }

  override fun groupBotton(beanDetail: DadosDevProduto): String {
    val finalTroca = if (beanDetail.produtoTrocaEnum == EProdutoTroca.Misto) {
      beanDetail.produtoTrocaItemEnum?.codigo ?: ""
    } else {
      beanDetail.produtoTrocaEnum ?: ""
    }
    return "$finalTroca - NI ${beanDetail.ni} NF ${beanDetail.nfDevolucao} DATA ${beanDetail.dataDevolucao.format()} - ${beanDetail.loginTroca}"
  }

  override fun printTitle(bean: DadosDevProduto) {
    writeln("Loja: ${bean.loja}", negrito = true)
    writeln(titulo, negrito = true)
    writeln("Data: ${bean.dataDevolucao.format()}", negrito = true)
    writeln("Usuario da Impressao: ${AppConfig.userLogin()?.name}", negrito = true)

    printLine('-')
  }

  override fun printSumary(bean: DadosDevProduto?) {
    val entregueNome = bean?.userEntregaName ?: ""
    val recebidoNome = bean?.userRecebimentoName ?: ""

    writeln("")
    writeln("")
    writeln("DOCUMENTO NÃO FISCAL", center = true)
    writeln("")
    writeln("")
    writeln("____________________________________", center = true)
    if (entregueNome.isNotBlank()) {
      writeln(entregueNome ?: "", center = true)
    }
    writeln("Separado/Entregue", center = true)
    writeln("${bean?.dataEntrega.format()} - ${bean?.horaEntrega.format("HH:mm")}", center = true)
    writeln("")
    writeln("")
    writeln("____________________________________", center = true)
    if (recebidoNome.isNotBlank()) {
      writeln(recebidoNome ?: "", center = true)
    }
    writeln("Recebido", center = true)
    writeln("${bean?.dataRecebimento.format()} - ${bean?.horaRecebimento.format("HH:mm")}", center = true)
    writeln("")
    writeln("")
  }
}