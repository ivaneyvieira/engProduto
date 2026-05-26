package br.com.astrosoft.produto.model.report

import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.nfeXml.IItensNotaReport

object RelatorioEspelhoNotaVenda {
  fun processaRelatorio(listNota: List<NotaRecebimentoDev>): ByteArray {
    val listItem: List<List<IItensNotaReport>> = listNota.toListItemVenda()
    return DanfeReport.create(listItem, ETIPO_COPIA.ESPELHO)
  }
}

fun List<NotaRecebimentoDev>.toListItemVenda(): List<List<IItensNotaReport>> {
  return this.map { nota ->
    nota.toListItemVenda()
  }
}

fun NotaRecebimentoDev.toListItemVenda(): List<IItensNotaReport> {
  return this.produtos
    .sortedBy { it.seq ?: 0 }
    .map { produto ->
      NotaRecebimentoDevItemVenda(this, produto)
    }
}

