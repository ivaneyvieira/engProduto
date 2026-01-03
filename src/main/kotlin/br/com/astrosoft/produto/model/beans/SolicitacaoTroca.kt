package br.com.astrosoft.produto.model.beans

data class SolicitacaoTroca(
  val solicitacaoTrocaEnnum: ESolicitacaoTroca,
  val produtoTrocaEnnum: EProdutoTroca,
  val nfEntRet: Int?,
  val motivo: EMotivoTroca,
)
