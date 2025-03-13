package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.produto.model.beans.ProdutoEstoque

interface IModelConferencia {
  fun updateProduto(bean: ProdutoEstoque?, updateGrid: Boolean)
}