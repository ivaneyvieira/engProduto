package br.com.astrosoft.produto.viewmodel.estoqueCD

import br.com.astrosoft.produto.model.beans.ProdutoEstoque

interface IModelConferencia {
  fun updateConferencia(bean: ProdutoEstoque?)
  fun updateLocalizacao(bean: ProdutoEstoque?)
  fun itensSelecionados(): List<ProdutoEstoque>
}