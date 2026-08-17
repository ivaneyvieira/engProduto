package br.com.astrosoft.produto.model.beans

data class SolicitacaoTroca(
  val solicitacaoTrocaEnnum: ESolicitacaoTroca,
  val produtoTrocaEnum: EProdutoTroca,
  val nfEntRet: Int?,
  val motivo: EMotivoTroca,
  val login: String,
  val senha: String
)

data class SolicitacaoTrocaSimples(
  val solicitacaoTrocaEnnum: ESolicitacaoTroca,
  val produtoTrocaEnum: EProdutoTroca,
  val nfEntRet: Int?,
)
