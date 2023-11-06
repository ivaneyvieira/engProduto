package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ProdutoRetiraEntrega(
  var loja: Int,
  var pedido: Int,
  var data: LocalDate?,
  var nota: String?,
  var tipo: String?,
  var cliente: Int?,
  var empno: Int?,
  var vendno: Int?,
  var codigo: String?,
  var descricao: String?,
  var grade: String?,
  var quant: Int?,
  var estSaci: Int?,
  var saldo: Int?,
  var typeno: Int?,
  var typeName: String?,
  var clno: String?,
  var clname: String?,
  var localizacao: String?,
  var gradeAlternativa: String?,
                          ) {
  fun findGrades(): List<PrdGrade> {
    val cod = codigo ?: return emptyList()
    return saci.findGrades(cod)
  }

  fun salvaGrade() = saci.gravaGrade(this)

  companion object {
    fun find(filtro: FiltroProduto): List<ProdutoRetiraEntrega> {
      return saci.findRetiraEntrega(filtro)
    }
  }
}

data class FiltroProduto(val loja: Int,
                         val codigo: String,
                         val typeno: Int,
                         val clno: Int,
                         val vendno: Int,
                         val localizacao: String,
                         val nota: String,
                         val isEdit: Boolean = false) {
  val prdno
    get() = if (codigo == "") "" else codigo.lpad(16, " ")
  val nfno
    get() = nota.split("/").getOrNull(0)?.toIntOrNull() ?: 0
  val nfse
    get() = nota.split("/").getOrNull(1) ?: ""
}