package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.produto.model.beans.NotaRecebimento
import java.time.LocalDate

class PrintNotaDoc : PrintText<NotaRecebimento>() {
  override fun printTitle(bean: NotaRecebimento) {
    writeln("Relatório de Notas Fiscais de Entrada Recebidas", negrito = true, center = true)
    writeln("Loja : ${bean.loja} - ${bean.lojaSigla}", negrito = true)
    writeln("Protocolo: ${bean.protocolo}", negrito = true)
    printLine()
  }

  init {
    column(NotaRecebimento::niStr, "NI", 7)
    column(NotaRecebimento::nfEntradaStr, "NF Ent", 12)
    column(NotaRecebimento::emissaoStr, "Emissao", 8)
    column(NotaRecebimento::dataStr, "Entrada", 8)
    column(NotaRecebimento::fornecedorSigla, "Fornecedor", 12)
    column(NotaRecebimento::valorNF, "Valor", 12)
  }

  override fun printSumary(bean: NotaRecebimento?) {
    writeln("")
    writeln("")
    writeln("_______________________________________", center = true)
    writeln(bean?.nomeEnvio ?: "", center = true)
    writeln("Envio", center = true)
    writeln("")
    writeln("_______________________________________", center = true)
    writeln(bean?.nomeReceb ?: "", center = true)
    writeln("Recebido", center = true)
    writeln("")
    writeln("")
    writeln("Teresina-PI, _______/______/________", center = true)
  }
}
