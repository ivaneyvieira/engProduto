package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.Config
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class ProdutoNF(
  val loja: Int,
  val pdvno: Int,
  val xano: Long,
  val codigo: String,
  val grade: String,
  val barcode: String,
  val descricao: String,
  val vendno: Int,
  val fornecedor: String,
  val typeno: Int,
  val typeName: String,
  val clno: String,
  val clname: String,
  val altura: Int,
  val comprimento: Int,
  val largura: Int,
  val precoCheio: Double,
  val ncm: String,
  val localizacao: String?,
  val quantidade: Int,
  val preco: Double,
  val total: Double,
  var gradeAlternativa: String?,
  var marca: Int,
  var usuario: String,
  var data_hora: String,
               ) {
  fun salva() {
    data_hora = LocalDate.now().format() + "_" + LocalTime.now().format()
    usuario = Config.user?.login ?: ""
    saci.salvaProdutosNFS(this)
  }

  fun findGrades(): List<PrdGrade> {
    return saci.findGrades(codigo)
  }
}
