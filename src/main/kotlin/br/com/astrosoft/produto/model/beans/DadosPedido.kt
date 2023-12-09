package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class DadosPedido(val loja: Int, val pedido: Int, val data: LocalDate?, val cliente: Int?, val empno: Int?) {
  fun expira() {
    saci.statusPedido(this, EStatusPedido.Expirado)
  }
}