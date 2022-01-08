package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class ProdutoNF(val loja: Int,
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
                var usuario: String) {
  fun salva() {
    saci.salvaProdutosNFS(this)
  }

  fun findGrades(): List<PrdGrade> {
    return saci.findGrades(codigo)
  }
}
