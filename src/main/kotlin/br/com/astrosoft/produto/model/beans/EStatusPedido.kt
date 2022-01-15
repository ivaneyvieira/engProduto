package br.com.astrosoft.produto.model.beans

enum class EStatusPedido(val codigo: Int) {
  Incluido(0), Orcado(1), Reservado(2),

  Vendido(3), Expirado(4), Cancelado(5),

  ReservaB(6), Transito(7), Futura(8)
}