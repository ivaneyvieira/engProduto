package br.com.astrosoft.produto.model.beans

class ProdutoRessuprimentoSobra(
  val codigo: String,
  val descricao: String,
  val grade: String,
  val nota: String,
  val quantidade: Int,
  val codigoSobra: String,
  val descricaoSobra: String,
  val gradeSobra: String,
  val quantidadeSobra: Int,
  val espaco: String = ""
)