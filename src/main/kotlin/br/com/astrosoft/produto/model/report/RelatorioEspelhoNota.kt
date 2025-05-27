package br.com.astrosoft.produto.model.report

import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.nfeXml.IItensNotaReport

object RelatorioEspelhoNota {
  fun processaRelatorio(listNota: List<NotaRecebimentoDev>): ByteArray {
    val listItem : List<List<IItensNotaReport>> = listNota.toListItem()
    return DanfeReport.create(listItem, ETIPO_COPIA.REIMPRESSAO)
  }
}

fun List<NotaRecebimentoDev>.toListItem(): List<List<IItensNotaReport>> {
  return this.map { nota ->
    nota.toListItem()
  }
}

fun NotaRecebimentoDev.toListItem(): List<IItensNotaReport> {
  return this.produtos.map {produto -> 
    NotaRecebimentoDevItem(this, produto)
  }
}

