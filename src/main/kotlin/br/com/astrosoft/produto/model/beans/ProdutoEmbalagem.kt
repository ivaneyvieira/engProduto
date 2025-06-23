package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoEmbalagem {
  var prdno: String? = null
  var qtdEmbalagem: Double? = null

  companion object {
    private var dateTime = System.currentTimeMillis()
    private val INTERVALO = 1000 * 60 * 5 // 5 minutos
    private val listEmbalagem = mutableListOf<ProdutoEmbalagem>()

    private fun findEmbalagem(): List<ProdutoEmbalagem> {
      return saci.findProdutoEmbalagem()
    }

    private fun updateEmbalagem() {
      listEmbalagem.clear()
      listEmbalagem.addAll(findEmbalagem())
    }

    private fun updateEmbalagemIfNeeded() {
      val intervaloUlt = System.currentTimeMillis() - dateTime > INTERVALO
      val embalagemVazia = listEmbalagem.isEmpty()
      if (intervaloUlt || embalagemVazia) {
        dateTime = System.currentTimeMillis()
        updateEmbalagem()
      }
    }

    fun findEmbalagem(prdno: String): ProdutoEmbalagem? {
      updateEmbalagemIfNeeded()
      return listEmbalagem.firstOrNull { it.prdno == prdno }
    }
  }
}