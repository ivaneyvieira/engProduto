package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoReserva(
  var loja: Int,
  var pedido: Int,
  var data: LocalDate?,
  var cliente: Int?,
  var vendno: Int?,
  var codigo: String?,
  var empno: Int?,
  var descricao: String?,
  var grade: String?,
  var reserva: Int?,
  var estSaci: Int?,
  var saldo: Int?,
  var typeno: Int?,
  var typeName: String?,
  var clno: String?,
  var clname: String?,
  var localizacao: String?,
                    ) {
  fun pedido(): Pedido = Pedido(loja, pedido, data, cliente, empno)

  companion object {
    fun find(filtro: FiltroProduto): List<ProdutoReserva> {
      return saci.findProdutoReserva(filtro)
    }
  }
}