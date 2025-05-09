package br.com.astrosoft.produto.viewmodel.devForRecebe

import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev
import br.com.astrosoft.produto.model.beans.PrdGrade

interface ITabNotaViewModel {
  fun findProdutos(codigo: String): List<PrdGrade>
  fun addProduto(produto: NotaRecebimentoProdutoDev?)
  fun updateProduto(produto: NotaRecebimentoProdutoDev, grade: String?)
}