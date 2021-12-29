package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoReserva(
  val loja: Int,
  val pedido: Int,
  val data: LocalDate,
  val cliente: Int,
  val vendno: Int,
  val codigo: String,
  val empno: Int,
  val descricao: String,
  val grade: String,
  val reserva: Int,
  val estSaci: Int,
  val saldo: Int,
  val typeno: Int,
  val typeName: String,
  val clno: String,
  val clname: String,
  val localizacao: String,
                    ){
  companion object {
    fun find(filtro: FiltroProduto): List<ProdutoReserva> {
      return saci.findProdutoReserva(filtro)
    }
  }
}