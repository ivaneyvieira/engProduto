package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoRetiraEntrega(
  val loja: Int,
  val pedido: Int,
  val data: LocalDate,
  val nota: String,
  val tipo: String,
  val cliente: Int,
  val empno: Int,
  val vendno: Int,
  val codigo: String,
  val descricao: String,
  val grade: String,
  val quant: Int,
  val estSaci: Int,
  val saldo: Int,
  val typeno: Int,
  val typeName: String,
  val clno: String,
  val clname: String,
                          ) {

  companion object {
    fun find(filtro: FiltroProduto): List<ProdutoRetiraEntrega> {
      return saci.findRetiraEntrega(filtro)
    }
  }
}

data class FiltroProduto(val codigo: String, val typeno: Int, val clno: Int, val vendno: Int, val nota: String) {
  val prdno
    get() = if (codigo == "") "" else codigo.lpad(16, " ")
  val nfno
    get() = nota.split("/").getOrNull(0)?.toIntOrNull() ?: 0
  val nfse
    get() = nota.split("/").getOrNull(1) ?: ""
}