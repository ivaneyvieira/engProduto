package br.com.astrosoft.produto.model.beans

data class Relatorio(
  val loja: String,
  val pedido: String,
  val dataPedido: String,
  val notaFiscal : String,
  val dataHoraNota: String,
  val rota: String,
  val vendedor: String,
  val cliente: String,
  val codigo: String,
  val descricao: String,
  val grade: String,
  val localizacao: String,
  val quantidade: Int,
                    )
